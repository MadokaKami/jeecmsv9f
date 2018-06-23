package com.jeecms.cms.action.member;

import static com.jeecms.cms.Constants.TPLDIR_MEMBER;

import java.io.Serializable;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.jeecms.cms.api.Constants;
import com.jeecms.cms.api.ResponseCode;
import com.jeecms.cms.web.Token;
import com.jeecms.common.email.EmailSender;
import com.jeecms.common.email.MessageTemplate;
import com.jeecms.common.security.encoder.PwdEncoder;
import com.jeecms.common.web.RequestUtils;
import com.jeecms.common.web.session.SessionProvider;
import com.jeecms.core.entity.CmsConfig;
import com.jeecms.core.entity.CmsConfigAttr;
import com.jeecms.core.entity.CmsSite;
import com.jeecms.core.entity.MemberConfig;
import com.jeecms.core.entity.UnifiedUser;
import com.jeecms.core.manager.CmsConfigMng;
import com.jeecms.core.manager.ConfigMng;
import com.jeecms.core.manager.UnifiedUserMng;
import com.jeecms.core.web.WebErrors;
import com.jeecms.core.web.util.CmsUtils;
import com.jeecms.core.web.util.FrontUtils;
import com.octo.captcha.service.CaptchaServiceException;
import com.octo.captcha.service.image.ImageCaptchaService;

/**
 * 找回密码Action
 * 
 * 用户忘记密码后点击找回密码链接，输入用户名、邮箱和验证码<li>
 * 如果信息正确，返回一个提示页面，并发送一封找回密码的邮件，邮件包含一个链接及新密码，点击链接新密码即生效<li>
 * 如果输入错误或服务器邮箱等信息设置不完整，则给出提示信息<li>
 */
@Controller
public class ForgotPasswordAct {
	private static Logger log = LoggerFactory
			.getLogger(ForgotPasswordAct.class);

	public static final String FORGOT_PASSWORD_INPUT = "tpl.forgotPasswordInput";
	public static final String FORGOT_PASSWORD_RESULT = "tpl.forgotPasswordResult";
	public static final String PASSWORD_RESET = "tpl.passwordReset";
	public static final String PASSWORD_SMS_RESET = "tpl.setNewPassword";

	/**
	 * 找回密码输入页
	 * 
	 * @param request
	 * @param response
	 * @param models
	 * @return
	 */
	@Token(save=true)
	@RequestMapping(value = "/member/forgot_password.jspx", method = RequestMethod.GET)
	public String forgotPasswordInput(HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		CmsConfig config = site.getConfig();
		
		Integer validateType = 0;
		if(config.getValidateType() != null) {
			validateType = config.getValidateType();			
		}
		Integer isSMSForgotPassword = 0;//是否开启SMS找回密码
		if(validateType == 2) {
			isSMSForgotPassword  = 1;
		}

		model.addAttribute("isSMSForgotPassword", isSMSForgotPassword);
		FrontUtils.frontData(request, model, site);
		return FrontUtils.getTplPath(request, site.getSolutionPath(),
				TPLDIR_MEMBER, FORGOT_PASSWORD_INPUT);
	}

	/**
	 * 找回密码提交页
	 * 
	 * @param username
	 * @param email
	 * @param captcha
	 * @param request
	 * @param response
	 * @return
	 */
	@Token(remove=true)
	@RequestMapping(value = "/member/forgot_password.jspx", method = RequestMethod.POST)
	public String forgotPasswordSubmit(String email,
			String captcha, HttpServletRequest request,
			HttpServletResponse response, ModelMap model,String mobile) {
		CmsSite site = CmsUtils.getSite(request);
		CmsConfig config = site.getConfig();
		WebErrors errors = WebErrors.create(request);
		String username=RequestUtils.getQueryParam(request,"username");

		Boolean isSMSForgotPassword = false;//是否开启SMS找回密码
		if (config.getValidateType() == 2) {
			isSMSForgotPassword = true;
		}

		if(isSMSForgotPassword) {
			errors = validateSMSForgotPasswordSubmit(username, mobile,
					captcha, request, response);
		}else {
			errors = validateForgotPasswordSubmit(username, email,
					captcha, request, response);			
		}

		if (errors.hasErrors()) {
			return FrontUtils.showError(request, response, model, errors);
		}
		if(isSMSForgotPassword) {
			//把用户保存到SESSION中
			UnifiedUser user = unifiedUserMng.getByUsername(username);
			session.setAttribute(request, response, "FOTGOTPWD_USER_ID", user.getId());	
			//跳转到创建新密码页面
			model.addAttribute("username", username);
			FrontUtils.frontData(request, model, site);
			return FrontUtils.getTplPath(request, site.getSolutionPath(),
					TPLDIR_MEMBER, PASSWORD_SMS_RESET);
		}
		UnifiedUser user = unifiedUserMng.getByUsername(username);
		EmailSender sender = configMng.getEmailSender();
		MessageTemplate msgTpl = configMng.getForgotPasswordMessageTemplate();
		model.addAttribute("user", user);
		FrontUtils.frontData(request, model, site);
		if (user == null) {
			// 用户名不存在
			model.addAttribute("status", 1);
		} else if (StringUtils.isBlank(user.getEmail())) {
			// 用户没有设置邮箱
			model.addAttribute("status", 2);
		} else if (!user.getEmail().equals(email)) {
			// 邮箱输入错误
			model.addAttribute("status", 3);
		} else if (sender == null) {
			// 邮件服务器没有设置好
			model.addAttribute("status", 4);
		} else if (msgTpl == null) {
			// 邮件模板没有设置好
			model.addAttribute("status", 5);
		} else {
			try {
				unifiedUserMng.passwordForgotten(user.getId(), sender, msgTpl);
				model.addAttribute("status", 0);
			} catch (Exception e) {
				// 发送邮件异常
				model.addAttribute("status", 100);
				model.addAttribute("message", e.getMessage());
				log.error("send email exception.", e);
			}
		}
		return FrontUtils.getTplPath(request, site.getSolutionPath(),
				TPLDIR_MEMBER, FORGOT_PASSWORD_RESULT);
	}
	
	@RequestMapping(value = "/member/password_reset.jspx", method = RequestMethod.GET)
	public String passwordReset(Integer uid, String key,
			HttpServletRequest request, HttpServletResponse response,
			ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		WebErrors errors = validatePasswordReset(uid, key, request);
		if (errors.hasErrors()) {
			return FrontUtils.showError(request, response, model, errors);
		}
		UnifiedUser user = unifiedUserMng.findById(uid);
		if (user == null) {
			// 用户不存在
			model.addAttribute("status", 1);
		} else if (StringUtils.isBlank(user.getResetKey())) {
			// resetKey不存在
			model.addAttribute("status", 2);
		} else if (!user.getResetKey().equals(key)) {
			// 重置key错误
			model.addAttribute("status", 3);
		} else {
			unifiedUserMng.resetPassword(uid);
			model.addAttribute("status", 0);
		}
		FrontUtils.frontData(request, model, site);
		return FrontUtils.getTplPath(request, site.getSolutionPath(),
				TPLDIR_MEMBER, PASSWORD_RESET);
	}
	
	//SMS验证密码修改
	@RequestMapping(value="sms_password_reset.jspx",method=RequestMethod.POST)
	public String smsRestPassword(String username,String loginPassword,HttpServletRequest request, 
			HttpServletResponse response,ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		WebErrors errors = WebErrors.create(request);
		//获取session中的用户ID
		Integer userId = (Integer) session.getAttribute(request,"FOTGOTPWD_USER_ID");
		
		if(StringUtils.isBlank(username) || StringUtils.isBlank(loginPassword)) {
			errors.addErrorString(Constants.API_MESSAGE_PARAM_REQUIRED);
			errors.addErrorString(ResponseCode.API_CODE_PARAM_REQUIRED);
		}else if(userId == null) {
			//验证过期
			errors.addErrorString(Constants.API_MESSAGE_USER_STATUS_OVER_TIME);
			errors.addErrorString(ResponseCode.API_CODE_USER_STATUS_OVER_TIME);
		}
		if(errors.hasErrors()) {
			return FrontUtils.showError(request, response, model, errors);
		}
		UnifiedUser user = unifiedUserMng.findById(userId);
		if(user == null) {
			errors.addErrorString(Constants.API_MESSAGE_USER_NOT_FOUND);
			errors.addErrorString(ResponseCode.API_CODE_USER_NOT_FOUND);
		}else if(!username.equals(user.getUsername())) {
			errors.addErrorString(Constants.API_MESSAGE_PARAM_ERROR);
			errors.addErrorString(ResponseCode.API_CODE_PARAM_ERROR);
		}

		if(errors.hasErrors()) {
			return FrontUtils.showError(request, response, model, errors);
		}else {
			user.setPassword(pwdEncoder.encodePassword(loginPassword));	
			unifiedUserMng.restPassword(user);
			model.addAttribute("status", 0);
		}
		FrontUtils.frontData(request, model, site);
		return FrontUtils.getTplPath(request, site.getSolutionPath(),
				TPLDIR_MEMBER, PASSWORD_RESET);
	}
	
	
	
	
	
	
	private WebErrors validateForgotPasswordSubmit(String username,
			String email, String captcha, HttpServletRequest request,
			HttpServletResponse response) {
		WebErrors errors = WebErrors.create(request);
		if (errors.ifBlank(username, "username", 100, true)) {
			return errors;
		}
		if (errors.ifBlank(email, "email", 100, true)) {
			return errors;
		}
		if (errors.ifBlank(captcha, "captcha", 20, true)) {
			return errors;
		}
		try {
			if (!imageCaptchaService.validateResponseForID(session
					.getSessionId(request, response), captcha)) {
				errors.addErrorCode("error.invalidCaptcha");
				return errors;
			}
		} catch (CaptchaServiceException e) {
			errors.addErrorCode("error.exceptionCaptcha");
			log.warn("", e);
			return errors;
		}
		return errors;
	}
	
	/**
	 * 校验SMS找回密码
	 * */
	private WebErrors validateSMSForgotPasswordSubmit(String username,
			String mobile, String captcha, HttpServletRequest request,
			HttpServletResponse response) {
		WebErrors errors = WebErrors.create(request);
		if (errors.ifBlank(username, "username", 100, true)) {
			return errors;
		}
		if (errors.ifBlank(mobile, "mobile", 20, true)) {
			return errors;
		}
		if (errors.ifBlank(captcha, "captcha", 20, true)) {
			return errors;
		}
		Serializable autoCodeTime = session.getAttribute(request, "FORGOTPWD_AUTO_CODE_CREAT_TIME");// 验证码有效时间
		Serializable autoCode = session.getAttribute(request, "FORGOTPWD_AUTO_CODE");// 验证码值
		// 判断验证码是否在有效时间范围
		if (autoCodeTime != null && autoCode != null) {
			Long effectiveTime = Long.parseLong(autoCodeTime.toString());
			if (effectiveTime > new Date().getTime()) {
				// 验证码验证码是否正确
				if (captcha.equals(autoCode.toString())) {
					session.setAttribute(request, response, "FORGOTPWD_AUTO_CODE_CREAT_TIME", null);					
				} else {
					// 验证码不正确
					errors.addErrorCode("error.invalidCaptcha");
				}
			} else {
				// 验证码失效
				errors.addErrorCode("error.invalidCaptcha");//loseEfficacyCaptcha
			}
		} else {
			// 验证码错误
			errors.addErrorCode("error.invalidCaptcha");
		}
		return errors;
	}

	private WebErrors validatePasswordReset(Integer uid, String key,
			HttpServletRequest request) {
		WebErrors errors = WebErrors.create(request);
		if (errors.ifNull(uid, "uid", true)) {
			return errors;
		}
		if (errors.ifBlank(key, "key", 50, true)) {
			return errors;
		}
		return errors;
	}

	@Autowired
	private UnifiedUserMng unifiedUserMng;
	@Autowired
	private ConfigMng configMng;
	@Autowired
	private SessionProvider session;
	@Autowired
	private ImageCaptchaService imageCaptchaService;
	@Autowired
	private PwdEncoder pwdEncoder;
}
