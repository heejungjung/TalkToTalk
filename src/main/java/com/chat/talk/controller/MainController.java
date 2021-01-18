package com.chat.talk.controller;

import java.io.File;
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

import com.chat.talk.model.Files;
import com.chat.talk.model.User;
import com.chat.talk.repository.FilesRepository;
import com.chat.talk.repository.UserRepository;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

@Controller
@SessionAttributes("username")
public class MainController {

	@Autowired
	private FilesRepository filesRepository;
	
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private User user;
    
    @Autowired
	private Files file;

    //환경변수
    @Value("${dir}")
    private String dir;

    //환경변수
    @Value("${ip}")
    private String ip;

    //메인홈
	@GetMapping("/")
	public String index(Model model) {
		List<Files> files = filesRepository.findAll();
	    Collections.shuffle(files);
		model.addAttribute("files", files);

		List<User> users = userRepository.findAll();
	    Collections.shuffle(users);
		model.addAttribute("users", users);
		return "index";
	}
	
	//홈 navbar 유저 프로필 사진 가져오기 위함
	@ResponseBody
	@RequestMapping(value="/homegetinfo", method=RequestMethod.POST)
	public Map<String, Object> homegetinfo(HttpServletRequest request, HttpServletResponse response,Model model,
			@RequestParam("username") String username) {
		Map<String, Object> userInfo = new HashMap<String, Object>();
		file = filesRepository.findByUsername(username);
		userInfo.put("pic", file.getFileurl()+file.getFilename());
		List<Files> files = filesRepository.findAll();
	    Collections.shuffle(files);
		model.addAttribute("files", files);
		return userInfo;
	}

	//로그인 과정
	@GetMapping("/logging")
	public String index(HttpServletRequest request, HttpServletResponse response,Model model,@RequestParam("username") String username) {
		user = new User();
		user = userRepository.findByUsername(username);
		request.getSession().setAttribute("nickname", user.getNickname());
		request.getSession().setAttribute("username", user.getUsername());

		List<Files> files = filesRepository.findAll();
	    Collections.shuffle(files);
		model.addAttribute("files", files);
    	
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
	public String mypaging(HttpServletRequest request, HttpServletResponse response,Model model,
			@RequestParam("username") String username,@RequestParam("nickname") String nickname) throws ParseException {
		user = new User();
		user = userRepository.findByUsername(username);

		file = new Files();
		file = filesRepository.findByUsername(username);
		
		DateFormat dateFormat = new SimpleDateFormat ("yyyy-MM-dd");
		Calendar today = Calendar.getInstance();
		Calendar d_day = Calendar.getInstance();
		d_day.setTime(dateFormat.parse(user.getRegdt()));
		long l_today = today.getTimeInMillis()/(24*60*60*1000);
		long l_d_day = d_day.getTimeInMillis()/(24*60*60*1000);
		
		request.getSession().setAttribute("pic", file.getFileurl()+file.getFilename());
		request.getSession().setAttribute("id", username);
		request.getSession().setAttribute("nickname", nickname);
		request.getSession().setAttribute("sex", user.getSex());
		request.getSession().setAttribute("birthday", user.getBirthday());
		request.getSession().setAttribute("email", user.getEmail());
		request.getSession().setAttribute("bio", file.getMessage());
		request.getSession().setAttribute("city", user.getCity());
		request.getSession().setAttribute("days", l_today-l_d_day);
		
		return "/mypage";
	}

	//마이페이지 프로필 사진 가져오기
	@ResponseBody
	@RequestMapping(value="/mypagegetpic", method=RequestMethod.POST)
	public Map<String, Object> mypagegetpic(HttpServletRequest request, HttpServletResponse response,Model model,
			@RequestParam("username") String username) {
		file = new Files();
		file = filesRepository.findByUsername(username);
		Map<String, Object> userInfo = new HashMap<String, Object>();
		
		userInfo.put("pic", file.getFileurl()+file.getFilename());
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

		file = new Files();
		file = filesRepository.findByUsername(username);
		
		if(user != null){
	        personJson = "{\"id\":\""+username
	                    +"\",\"sex\":\""+user.getSex()
	                    +"\",\"birthday\":\""+user.getBirthday()
	                    +"\",\"city\":\""+user.getCity()
	                    +"\",\"picture\":\""+file.getFileurl()+file.getFilename()
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
    	filesRepository.updateMessage(message,username);
    	file = filesRepository.findByUsername(username);
        
        return file.getMessage();
    }

	//프로필사진 변경
	@RequestMapping(value = "/imgchange", method = RequestMethod.POST, consumes = {"multipart/form-data"})
    public String imgchange(@RequestPart MultipartFile file, HttpServletRequest request) throws Exception{
		String username = (String) request.getSession(true).getAttribute("username");
		String sourceFileName = file.getOriginalFilename();
    	filesRepository.updaterawname(sourceFileName,username);
    	filesRepository.updateFileurl("/images/"+username+"/",username);

		//파일 첨부 여부
		if(sourceFileName != null && !sourceFileName.equals("")) {
			File destinationFile;
			String destinationFileName;
			String fileUrl = dir+username+"/";

			destinationFileName = profiledate() + "_" + sourceFileName;
	    	filesRepository.updateFilename(destinationFileName,username);
			destinationFile = new File(fileUrl + destinationFileName);

			destinationFile.getParentFile().mkdirs();
			file.transferTo(destinationFile);

			//썸네일
			Thumbnails.of(fileUrl + destinationFileName).crop(Positions.CENTER).size(150, 150).toFile(new File(fileUrl,"s_"+destinationFileName));
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
