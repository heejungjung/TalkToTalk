package com.chat.talk.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.chat.talk.model.Profiles;
import com.chat.talk.model.User;
import com.chat.talk.repository.ProfilesRepository;
import com.chat.talk.repository.UserRepository;
import com.chat.talk.services.S3UploadService;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.springframework.util.StringUtils;

@Controller
@SessionAttributes("username")
public class MainController {

	@Autowired
	private ProfilesRepository ProfilesRepository;
	
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private User user;
    
    @Autowired
	private Profiles file;

	@Autowired
	private S3UploadService S3service;

    //환경변수
    @Value("${dir}")
    private String dir;

    //환경변수
    @Value("${ip}")
    private String ip;

    //메인홈
	@GetMapping("/")
	public String index(Model model) {
		List<Profiles> Profiles = ProfilesRepository.findAll();
	    Collections.shuffle(Profiles);
		model.addAttribute("Profiles", Profiles);

		List<User> users = userRepository.findAll();
	    Collections.shuffle(users);
		model.addAttribute("users", users);
		return "index";
	}
	
	//홈 navbar 유저 프로필 사진 가져오기 위함
	@ResponseBody
	@RequestMapping(value="/homegetinfo", method=RequestMethod.POST)
	public Map<String, Object> homegetinfo(HttpServletRequest request, HttpServletResponse response,Model model, @RequestParam("username") String username) {
		Map<String, Object> userInfo = new HashMap<String, Object>();
		file = ProfilesRepository.findByUsername(username);
		userInfo.put("pic", file.getFileurl());
		List<Profiles> Profiles = ProfilesRepository.findAll();
	    Collections.shuffle(Profiles);
		model.addAttribute("Profiles", Profiles);
		return userInfo;
	}

	//로그인 과정
	@GetMapping("/logging")
	public String index(HttpServletRequest request, HttpServletResponse response,Model model,@RequestParam("username") String username) {
		user = new User();
		user = userRepository.findByUsername(username);
		request.getSession().setAttribute("nickname", user.getNickname());
		request.getSession().setAttribute("username", user.getUsername());

		List<Profiles> Profiles = ProfilesRepository.findAll();
	    Collections.shuffle(Profiles);
		model.addAttribute("Profiles", Profiles);
    	
		return "index";
	}
	
	//채팅페이지
	@RequestMapping("/chat")
	public void chat() {
	}
	
    @RequestMapping("/mypage")
	public void mypage() {
	}

	//마이페이지 정보 가져오기
	@GetMapping("/mypaging")
	public String mypaging(HttpServletRequest request, HttpServletResponse response,Model model, @RequestParam("username") String username,@RequestParam("nickname") String nickname) throws ParseException {
		user = new User();
		user = userRepository.findByUsername(username);

		file = new Profiles();
		file = ProfilesRepository.findByUsername(username);
		
		DateFormat dateFormat = new SimpleDateFormat ("yyyy-MM-dd");
		Calendar today = Calendar.getInstance();
		Calendar d_day = Calendar.getInstance();
		d_day.setTime(dateFormat.parse(user.getRegdt().toString()));
		long l_today = today.getTimeInMillis()/(24*60*60*1000);
		long l_d_day = d_day.getTimeInMillis()/(24*60*60*1000);
		
		request.getSession().setAttribute("pic", file.getFileurl());
		request.getSession().setAttribute("id", username);
		request.getSession().setAttribute("nickname", nickname);
		request.getSession().setAttribute("sex", user.getSex());
		request.getSession().setAttribute("birthday", dateFormat.format(user.getBirthday()));
		request.getSession().setAttribute("email", user.getEmail());
		request.getSession().setAttribute("bio", file.getMessage());
		request.getSession().setAttribute("city", user.getCity());
		request.getSession().setAttribute("days", l_today-l_d_day);
		
		return "/mypage";
	}

	//마이페이지 프로필 사진 가져오기
	@ResponseBody
	@RequestMapping(value="/mypagegetpic", method=RequestMethod.POST)
	public Map<String, Object> mypagegetpic(HttpServletRequest request, HttpServletResponse response,Model model, @RequestParam("username") String username) {
		file = new Profiles();
		file = ProfilesRepository.findByUsername(username);
		Map<String, Object> userInfo = new HashMap<String, Object>();
		
		userInfo.put("pic", file.getFileurl());
		userInfo.put("bio", file.getMessage());
		
		return userInfo;
	}

	//유저페이지 정보 가져오기
	@GetMapping("/infopaging")
	public void infopaging(HttpServletRequest request, HttpServletResponse response,Model model,
			@RequestParam("username") String username) {
	    String personJson;
		
		user = new User();
		user = userRepository.findByUsername(username);

		file = new Profiles();
		file = ProfilesRepository.findByUsername(username);
		DateFormat dateFormat = new SimpleDateFormat ("yyyy-MM-dd");
		
		if(user != null){
	        personJson = "{\"id\":\""+username
	                    +"\",\"sex\":\""+user.getSex()
	                    +"\",\"birthday\":\""+dateFormat.format(user.getBirthday())
	                    +"\",\"city\":\""+user.getCity()
	                    +"\",\"picture\":\""+file.getFileurl()
	                    +"\",\"bio\":\""+file.getMessage()+"\"}";
	    }
	    else{
	        personJson = "null";
	    }
	    try {
	        response.setCharacterEncoding("UTF-8");
	        response.getWriter().print(personJson);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	//상태메시지 변경
	@ResponseBody
	@GetMapping("msgchange")
    public String msgchange(String message,String username) {
    	ProfilesRepository.updateMessage(message,username);
    	file = ProfilesRepository.findByUsername(username);
        
        return file.getMessage();
    }

	//프로필사진 변경
	@RequestMapping(value = "/imgchange", method = RequestMethod.POST, consumes = {"multipart/form-data"})
    public String imgchange(@RequestPart MultipartFile file, HttpServletRequest request) throws Exception{
		String username = (String) request.getSession(true).getAttribute("username");
		String sourceFileName = file.getOriginalFilename();
		String extension = StringUtils.getFilenameExtension(sourceFileName);
		//파일 첨부 여부
		if(sourceFileName != null && !sourceFileName.equals("")) {
			try {
				// 저장될 파일의 절대 경로 설정
				String fileName = username+profiledate()+"."+extension;
				// 파일 저장 & 파일 URL 설정
				String fileUrl = S3service.saveFile(file,fileName);
				ProfilesRepository.updaterawname(sourceFileName,username);
				ProfilesRepository.updateFileUrl(fileUrl,username);		
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Thread.sleep(2000);
		
    	return "/mypage";
    }

	private String profiledate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		String str = sdf.format(date);
		return str.replace("-","");
	}

	//로컬 프로젝트 실행을 위한 ip
	@GetMapping("/ipget")
	public void ipget(HttpServletRequest request, HttpServletResponse response,Model model,
			@RequestParam("hi") String hi) {
	    String personJson = "{\"ip\":\""+ip+"\"}";
	    try {
	        response.setCharacterEncoding("UTF-8");
	        response.getWriter().print(personJson);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
}
