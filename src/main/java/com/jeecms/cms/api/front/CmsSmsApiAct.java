package com.jeecms.cms.api.front;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.baidubce.auth.DefaultBceCredentials;
import com.baidubce.services.sms.SmsClient;
import com.baidubce.services.sms.SmsClientConfiguration;
import com.baidubce.services.sms.model.SendMessageV2Request;
import com.baidubce.services.sms.model.SendMessageV2Response;
import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.jeecms.cms.api.ApiResponse;
import com.jeecms.cms.api.ApiValidate;
import com.jeecms.cms.api.Constants;
import com.jeecms.cms.api.ResponseCode;
import com.jeecms.common.web.ResponseUtils;
import com.jeecms.common.web.session.SessionProvider;
import com.jeecms.core.entity.CmsConfig;
import com.jeecms.core.entity.CmsSite;
import com.jeecms.core.entity.CmsSms;
import com.jeecms.core.entity.CmsSmsRecord;
import com.jeecms.core.entity.CmsUser;
import com.jeecms.core.entity.CmsUserExt;
import com.jeecms.core.entity.UnifiedUser;
import com.jeecms.core.manager.CmsSmsMng;
import com.jeecms.core.manager.CmsSmsRecordMng;
import com.jeecms.core.manager.CmsUserExtMng;
import com.jeecms.core.manager.CmsUserMng;
import com.jeecms.core.manager.UnifiedUserMng;
import com.jeecms.core.web.WebErrors;
import com.jeecms.core.web.util.CmsUtils;
import com.octo.captcha.service.image.ImageCaptchaService;

@Controller
public class CmsSmsApiAct {
	/**
	 * 发送短信，若配置为手机注册-不需要验证图形验证码，若未配置-需要验证图形验证码
	 * @param mobilePhone 手机号
	 * @param SmsSendType 发送短信类型用途  1：注册   2：找回密码
	 * @param vCode		     验证码
	 * @param username 	     用户名      找回密码
	 * @param request
	 * @param response
	 */
	@RequestMapping("/sms/send_register_msg")
	public void send(Integer smsSendType,String mobilePhone,String vCode,String username,HttpServletRequest request,HttpServletResponse response,HttpSession session){
		String body = "\"\"";
		String message = Constants.API_MESSAGE_PARAM_REQUIRED;
		String code = ResponseCode.API_CODE_PARAM_REQUIRED;
		WebErrors errors = WebErrors.create(request);
		CmsSite site = CmsUtils.getSite(request);
		CmsConfig config = site.getConfig();
		errors = ApiValidate.validateRequiredParams(request, errors,mobilePhone);
		if (!errors.hasErrors()) {
			//查询是否开启短信验证
			Integer type = config.getValidateType();
			if(type != 2) {//验证类型：0：无验证 1：邮件验证  2：SMS验证
				errors.addErrorString(Constants.API_MESSAGE_SMS_IS_DISABLE);
				errors.addErrorString(ResponseCode.API_CODE_SMS_IS_DISABLE);
			}
			Long id = config.getSmsID();
			String smsId = "";
			if(id != null && id != 0) {
				smsId = id.toString();
			}
			
			if(smsSendType == 1) {//发送短信类型用途  1：注册   2：找回密码
				errors = validateRegister(config,smsId,mobilePhone, vCode, errors, request, response);
			}else if(smsSendType == 2){
				errors = validateForgotPassword(config,smsId,mobilePhone,vCode,errors, request, response,username);
			}else {
				errors.addErrorString(Constants.API_MESSAGE_PARAM_ERROR);
				errors.addErrorString(ResponseCode.API_CODE_PARAM_ERROR);
			}
			
			if (errors.hasErrors()) {
				message = errors.getErrors().get(0);
				code = errors.getErrors().get(1);					
			}else{
				//获取模板平台 1阿里 2腾讯 3百度
				CmsSms bean = null;
				if (StringUtils.isNotBlank(smsId)) {
					bean = manager.findById(Integer.parseInt(smsId));
					Byte source = bean.getSource();
					
					//创建验证码
					Random r = new Random();
					StringBuffer str = new StringBuffer();
					//验证码位数
					Integer num = 6;//默认6位
					if(bean.getRandomNum() != null && bean.getRandomNum() > 0) {
						num = bean.getRandomNum();
					}
					int i = 0;
					while(i < num) {
						str.append(r.nextInt(10));
						i++;
					}
					String values = str.toString();
		
					if (source.equals((byte)1)) {
						errors = sendByALi(bean, mobilePhone, values, errors,site,username,smsSendType);
					}else if (source.equals((byte)2)) {
						errors = sendByTX(bean, mobilePhone, values, errors,site,username,smsSendType);
					}else if (source.equals((byte)3)) {
						errors = sendByBaiDu(bean, mobilePhone, values, errors,site,username,smsSendType);
					}
					if (errors.hasErrors()) {
						message = Constants.API_MESSAGE_SMS_ERROR;
						code = ResponseCode.API_CODE_SMS_ERROR;
					}else{
						
						//获取验证码有效时间
						Integer effectiveTime = 3*60*60*1000;//系统默认三分钟有效
						Byte effectiveUnit = 1;
						if(bean.getEffectiveTime() != null && bean.getEffectiveTime() > 0) {
							effectiveTime=bean.getEffectiveTime();
							if(bean.getEffectiveUnit() != null) {
								effectiveUnit = bean.getEffectiveUnit();//获取有效时间单位  有效时间单位 0秒 1分 2时								
							}
							switch (effectiveUnit) {
							case 0:
								effectiveTime = effectiveTime * 1000;//秒-毫秒
								break;
							case 1:
								effectiveTime = effectiveTime *60 *1000;//分-毫秒
								break;
							case 2:
								effectiveTime = effectiveTime * 60 * 60 * 1000;//时-毫秒
								break;
							default:
								effectiveTime = effectiveTime * 1000;//秒-毫秒
								break;
							}	
						}
						if(smsSendType == 1) {//发送短信类型用途  1：注册   2：找回密码
							session.setAttribute("AUTO_CODE",values);//验证码值
							session.setAttribute("AUTO_CODE_CREAT_TIME",new Date().getTime()+effectiveTime);//验证码有效时间						
						}else if(smsSendType == 2){
							session.setAttribute("FORGOTPWD_AUTO_CODE",values);//验证码值
							session.setAttribute("FORGOTPWD_AUTO_CODE_CREAT_TIME",new Date().getTime()+effectiveTime);//验证码有效时间
						}
						
						message = Constants.API_MESSAGE_SUCCESS;
						code = ResponseCode.API_CODE_CALL_SUCCESS;
					}
				}else{
					message = Constants.API_MESSAGE_SMS_NOT_SET;
					code = ResponseCode.API_CODE_SMS_NOT_SET;
				}
			}
		}
		ApiResponse apiResponse = new ApiResponse(request, body, message, code);
		ResponseUtils.renderApiJson(response, request, apiResponse);
	}
	
	private WebErrors validateRegister(CmsConfig config,String smsId,
			String mobilePhone,String vCode,WebErrors errors,HttpServletRequest request,HttpServletResponse response){

		//判断验证码是否正确
		errors = validateCode(vCode,errors,request,response);			
		if (errors.hasErrors()) {
			return errors;
		}else{
			//判断手机号是否已注册
			int countByPhone = userExtManager.countByPhone(mobilePhone);
			if (countByPhone!=0) {
				errors.addErrorString(Constants.API_MESSAGE_MOBILE_PHONE_EXIST);
				errors.addErrorString(ResponseCode.API_CODE_MOBILE_PHONE_EXIST);
				return errors;
			}
			errors = validateSMS(config,mobilePhone, errors,smsId);
		}
		return errors;
	}

	/**
	 * 
	 * @Title: validateForgotPassword   
	 * @Description: 找回密码SMS验证
	 * @param:  attr
	 * @param:  isSMSRegister
	 * @param:  smsId
	 * @param:  mobilePhone
	 * @param:  vCode
	 * @param:  errors
	 * @param:  request
	 * @param:  response    
	 * @return: WebErrors
	 */
	private WebErrors validateForgotPassword(CmsConfig config,String smsId,
			String mobilePhone,String vCode,WebErrors errors,HttpServletRequest request,HttpServletResponse response,String username){
		if(StringUtils.isBlank(username)) {
			errors.addErrorString(Constants.API_MESSAGE_USER_NOT_FOUND);
			errors.addErrorString(ResponseCode.API_CODE_USER_NOT_FOUND);
		}else {
			//判断验证码是否正确
			errors = validateCode(vCode,errors,request,response);
		}
		if (errors.hasErrors()) {
			return errors;
		}else{
			//判断手机号与用户名是否匹配
			UnifiedUser user = unifiedUserMng.getByUsername(username);
			CmsUser user2 = cmsUserMng.findByUsername(username);
			CmsUserExt userExt =null;
			if(user2 != null) {
				 userExt = userExtManager.findById(user2.getId());				
			}
			if (user == null || user2 == null || userExt == null) {
				// 用户名不存在
				errors.addErrorString(Constants.API_MESSAGE_USER_NOT_FOUND);
				errors.addErrorString(ResponseCode.API_CODE_USER_NOT_FOUND);
			} else if (StringUtils.isBlank(userExt.getMobile())) {
				// 用户没有设置手机号码
				errors.addErrorString(Constants.API_MESSAGE_NOT_MOBILE);
				errors.addErrorString(ResponseCode.API_CODE_MOBILE_NOT_SET);
			}else {
				String mobile = userExt.getMobile();
				if(!mobile.equals(mobilePhone)) {
					//输入的手机号码与绑定的手机号不匹配
					errors.addErrorString(Constants.API_MESSAGE_MOBILE_MISMATCHING);
					errors.addErrorString(ResponseCode.API_CODE_MOBILE_MISMATCHING);
				}
			}
			errors = validateSMS(config,mobilePhone, errors,smsId);
		}
		return errors;
	}
	
	private WebErrors validateSMS(CmsConfig config,String mobilePhone, WebErrors errors, String smsId) {
		//判断手机号每日限制是否已达标
		List<CmsSmsRecord> findByPhone = smsRecordManager.findByPhone(mobilePhone);
		Integer dayCount = 0;
		if(config.getDayCount() != null) {
			dayCount = config.getDayCount();			
		}
		if (dayCount>0) {//每日限制若大于0则需要进行限制校验
			if (findByPhone.size() >= dayCount) {//比较当天发送记录是否达到每日限制次数
				errors.addErrorString(Constants.API_MESSAGE_SMS_LIMIT);
				errors.addErrorString(ResponseCode.API_CODE_SMS_LIMIT);
				return errors;
			} else if (findByPhone.size() > 0) {
				if (StringUtils.isNotBlank(smsId)) {
					CmsSms bean = manager.findById(Integer.parseInt(smsId));
					if (bean==null) {
						errors.addErrorString(Constants.API_MESSAGE_OBJECT_NOT_FOUND);
						errors.addErrorString(ResponseCode.API_CODE_NOT_FOUND);
						return errors;
					}else{
						CmsSmsRecord record = findByPhone.get(0);
						//判断手机号每条短信相隔时间
						//获取间隔时间
						long intervalTime = 1*60*60*1000;//系统默认间隔1分钟
						Byte intervalUnit =1;
						long sendTime = record.getSendTime().getTime();
						long currentTime = new Date().getTime();
						if(bean.getIntervalTime() != null && bean.getIntervalTime() > 0) {
							intervalTime = bean.getIntervalTime();							
						}
						if(bean.getIntervalUnit() != null) {
							intervalUnit = bean.getIntervalUnit();							
						}
						switch (intervalUnit) {
						case 0:
							intervalTime = intervalTime * 1000;//秒-毫秒
							break;
						case 1:
							intervalTime = intervalTime *60 *1000;//分-毫秒
							break;
						case 2:
							intervalTime = intervalTime * 60 * 60 * 1000;//时-毫秒
							break;
						default:
							intervalTime = intervalTime * 1000;//秒-毫秒
							break;
						}
						if (currentTime-sendTime<intervalTime) {
							//当前时间减去上一次的发送时间，若小于限制间隔时间，则终止发送
							errors.addErrorString(Constants.API_MESSAGE_INTERVAL_NOT_ENOUGH);
							errors.addErrorString(ResponseCode.API_CODE_INTERVAL_NOT_ENOUGH);
							return errors;
						}
					}
				}else{
					//未配置
					errors.addErrorString(Constants.API_MESSAGE_SMS_NOT_SET);
					errors.addErrorString(ResponseCode.API_CODE_SMS_NOT_SET);
					return errors;
				}
			} 
		}
		return errors;
	}
	
	private WebErrors validateCode(String vCode,WebErrors errors,HttpServletRequest request,HttpServletResponse response) {
		if (StringUtils.isBlank(vCode)) {
			errors.addErrorString(Constants.API_MESSAGE_CAPTCHA_CODE_ERROR);
			errors.addErrorString(ResponseCode.API_CODE_CAPTCHA_CODE_ERROR);
			return errors;
		}else{
			try {
				if (!imageCaptchaService.validateResponseForID(session.getSessionId(request, response), vCode)) {
					errors.addErrorString(Constants.API_MESSAGE_CAPTCHA_CODE_ERROR);
					errors.addErrorString(ResponseCode.API_CODE_CAPTCHA_CODE_ERROR);
					return errors;
				}
			} catch (Exception e) {
				errors.addErrorString(Constants.API_MESSAGE_CREATE_ERROR);
				errors.addErrorString(ResponseCode.API_CODE_CALL_FAIL);
			}
		}
		return errors;
	}

	private WebErrors sendByALi(CmsSms bean,String mobilePhone,String values,WebErrors errors, CmsSite site, String username, Integer smsSendType){
		try {
			//初始化ascClient
			IClientProfile profile = DefaultProfile.getProfile(CmsSms.regionId, bean.getAccessKeyId(), bean.getAccessKeySecret());
			DefaultProfile.addEndpoint(CmsSms.endpointName, CmsSms.regionId, CmsSms.product, CmsSms.domain);
			IAcsClient acsClient = new DefaultAcsClient(profile);
			//组装请求对象
			SendSmsRequest request = new SendSmsRequest();
			 //使用post提交
			 request.setMethod(MethodType.POST);
			 //必填:待发送手机号。支持以逗号分隔的形式进行批量调用，批量上限为1000个手机号码,批量调用相对于单条调用及时性稍有延迟,验证码类型的短信推荐使用单条调用的方式
			 request.setPhoneNumbers(mobilePhone);
			 //必填:短信签名-可在短信控制台中找到
			 request.setSignName(bean.getSignName());
			 //必填:短信模板-可在短信控制台中找到
			 request.setTemplateCode(bean.getTemplateCode());
			 //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
			 //友情提示:如果JSON中需要带换行符,请参照标准的JSON协议对换行符的要求,比如短信内容中包含\r\n的情况在JSON中需要表示成\\r\\n,否则会导致JSON在服务端解析失败
			 //格式化模板参数
			 String param = bean.getTemplateParam();
			 String[] split = null;
			 String[] value = null;
			 if (StringUtils.isNotBlank(param)) {
				split = param.split(",");
			 }
			 if (StringUtils.isNotBlank(values)) {
				value = values.split(",");
			 }
			 String templateParam = createParamForAli(split,value);
			 request.setTemplateParam(templateParam);
			 if (StringUtils.isNotBlank(bean.getSmsUpExtendCode())) {
				 //可选-上行短信扩展码(扩展码字段控制在7位或以下，无特殊需求用户请忽略此字段)
				 request.setSmsUpExtendCode("90997");
			 }				
			 if (StringUtils.isNotBlank(bean.getOutId())) {
				 //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
				 request.setOutId(bean.getOutId());
			 }
			 //请求失败这里会抛ClientException异常
			 SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
			 if(sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK")) {
				 //请求成功
				 CmsSmsRecord record = new CmsSmsRecord();
				 record.setPhone(mobilePhone);
				 record.setSendTime(new Date());
				 record.setSms(bean);

				 record.setSite(site);
				 record.setValidateType(smsSendType);
				 CmsUser user =null;
				 if(StringUtils.isNotBlank(username)) {
					 user = cmsUserMng.findByUsername(username);
				 }
				 record.setUser(user);
				 
				 smsRecordManager.save(record);
				 return errors;
			 }else{
				 errors.addErrorString(sendSmsResponse.getCode());
				 return errors;
			 }
		} catch (ClientException e) {
			e.printStackTrace();
			errors.addErrorString(Constants.API_MESSAGE_SMS_ERROR);
		}
		return errors;
	}
	private String createParamForAli(String[] param, String[] values) {
		JSONObject json = new JSONObject();
		if (param!=null && values!=null && param.length==values.length) {
			for (int i = 0; i < param.length; i++) {
				json.put(param[i], values[i]);
			}
		}
		return json.toString();
	}

	/***
	 * 
	 * @param username 
	 * @param site 
	 * @param smsSendType 
	 * @Title: sendByBaiDu   
	 * @Description: 百度短信服务
	 * @param: @param bean
	 * @param: @param mobilePhone
	 * @param: @param values
	 * @param: @param errors
	 * @param: @return      
	 * @return: WebErrors
	 */
	private WebErrors sendByBaiDu(CmsSms bean,String mobilePhone,String values,WebErrors errors, CmsSite site, String username, Integer smsSendType){
		// 相关参数定义
        String endPoint = bean.getEndPoint(); // SMS服务域名，可根据环境选择具体域名
        String accessKeyId = bean.getAccessKeyId();  // 发送账号安全认证的Access Key ID
        String secretAccessKy = bean.getAccessKeySecret(); // 发送账号安全认证的Secret Access Key
        // ak、sk等config
        SmsClientConfiguration config = new SmsClientConfiguration();
        config.setCredentials(new DefaultBceCredentials(accessKeyId, secretAccessKy));
        config.setEndpoint(endPoint);
        // 实例化发送客户端
        SmsClient smsClient = new SmsClient(config);
        
        // 定义请求参数
        String invokeId = bean.getInvokeId(); // 发送使用签名的调用ID
        String phoneNumber = mobilePhone; // 要发送的手机号码(只能填写一个手机号)
        String templateCode = bean.getTemplateCode(); // 本次发送使用的模板Code
        
        String param = bean.getTemplateParam();
        String[] split = null;
		String[] value = null;
		if (StringUtils.isNotBlank(param)) {
			split = param.split(",");
		}
		if (StringUtils.isNotBlank(values)) {
			value = values.split(",");
		}
		Map<String, String> vars =
				new HashMap<String, String>(); // 若模板内容为：您的验证码是${code},在${time}分钟内输入有效
		if (split!=null && value !=null && split.length==value.length) {
			for (int i = 0; i < split.length; i++) {
				vars.put(split[i], value[i]);
			}
		}
        //实例化请求对象
        SendMessageV2Request request = new SendMessageV2Request();
        request.withInvokeId(invokeId)
                .withPhoneNumber(phoneNumber)
                .withTemplateCode(templateCode)
                .withContentVar(vars);

        // 发送请求
        SendMessageV2Response response = smsClient.sendMessage(request);

        // 解析请求响应 response.isSuccess()为true 表示成功
        if (response != null && response.isSuccess()) {
        	//请求成功
			CmsSmsRecord record = new CmsSmsRecord();
			record.setPhone(mobilePhone);
			record.setSendTime(new Date());
			record.setSms(bean);

			record.setSite(site);
			CmsUser user =null;
			if(StringUtils.isNotBlank(username)) {
				user = cmsUserMng.findByUsername(username);
			}
			record.setUser(user);
			record.setValidateType(smsSendType);
			smsRecordManager.save(record);
			return errors;
        } else {
        	errors.addErrorString(response.getCode());
			return errors;
        }
	}
	
	/**
	 * 腾讯短信服务
	 * @param bean
	 * @param mobilePhone
	 * @param values
	 * @param errors
	 * @param username 
	 * @param site 
	 * @param smsSendType 
	 * @return
	 */
	private WebErrors sendByTX(CmsSms bean,String mobilePhone,String values,WebErrors errors, CmsSite site, String username, Integer smsSendType){
		try {
			SmsSingleSender sender = new SmsSingleSender(Integer.parseInt(bean.getAccessKeyId()),bean.getAccessKeySecret());
			ArrayList<String> params = new ArrayList<String>();
			String[] value = null;
			if (StringUtils.isNotBlank(values)) {
				value = values.split(",");
			}
			if (value!=null) {
				for (int i = 0; i < value.length; i++) {
					params.add(value[i]);
				}
			}
			if (bean.getTemplateCode()!=null) {
				SmsSingleSenderResult result = sender.sendWithParam(bean.getNationCode(),mobilePhone,Integer.parseInt(bean.getTemplateCode()), params, "", "", "");
				if (result.result==0) {
					//请求成功
					CmsSmsRecord record = new CmsSmsRecord();
					record.setPhone(mobilePhone);
					record.setSendTime(new Date());
					record.setSms(bean);

					record.setSite(site);
					CmsUser user =null;
					if(StringUtils.isNotBlank(username)) {
						user = cmsUserMng.findByUsername(username);
					}
					record.setUser(user);
					record.setValidateType(smsSendType);
					
					smsRecordManager.save(record);
					return errors;
				}else{
					errors.addErrorString(Constants.API_MESSAGE_SMS_ERROR);
					return errors;
				}
			}else{
				errors.addErrorString(Constants.API_MESSAGE_PARAM_ERROR);
				return errors;
			}
		} catch (Exception e) {
			errors.addErrorString(Constants.API_MESSAGE_SMS_ERROR);
		}
		return errors;
	}
	
	@Autowired
	private CmsSmsMng manager;
	@Autowired
	private CmsSmsRecordMng smsRecordManager;
	@Autowired
	private SessionProvider session;
	@Autowired
	private ImageCaptchaService imageCaptchaService;
	@Autowired
	private CmsUserExtMng userExtManager;
	@Autowired
	private UnifiedUserMng unifiedUserMng;
	@Autowired
	private CmsUserMng cmsUserMng;
}
