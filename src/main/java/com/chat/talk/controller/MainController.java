package com.chat.talk.controller;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
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
	private Files files;

	@GetMapping("/")
	public String index(Model model) {
		List<Files> files = filesRepository.findAll();
	    Collections.shuffle(files);
		model.addAttribute("files", files);
		return "index";
	}
	
	@ResponseBody
	@RequestMapping(value="/homegetinfo", method=RequestMethod.POST)
	public Map<String, Object> homegetinfo(HttpServletRequest request, HttpServletResponse response,Model model,
			@RequestParam("username") String username) {
		Map<String, Object> userInfo = new HashMap<String, Object>();
		files = filesRepository.findByUsername(userRepository.findByUsername(username).getUsername());
		userInfo.put("pic", files.getFileurl()+files.getFilename());
		List<Files> files = filesRepository.findAll();
	    Collections.shuffle(files);
		model.addAttribute("files", files);
		return userInfo;
	}
	
	@GetMapping("/test3")
	public String test3() {
		return "test3";
	}

	@GetMapping("/logging")
	public String index(HttpServletRequest request, HttpServletResponse response,Model model,
			@RequestParam("username") String username) {
		user = new User();
		user = userRepository.findByUsername(username);
		request.getSession().setAttribute("nickname", user.getNickname());
		request.getSession().setAttribute("username", user.getUsername());

    	//HttpSession session = request.getSession();
    	//request의 getSession() 메서드는 서버에 생성된 세션이 있다면 세션을 반환하고, 없다면 새 세션을 생성하여 반환한다. (인수 default가 true)
    	//파라미터로 false를 전달하면, 이미 생성된 세션이 있을 때 그 세션을 반환하고, 없으면 null을 반환한다.
    	//session.setAttribute("loginUser", new User(user1)); //세션의 속성 값은 객체 형태만
    	//웹 브라우저를 닫지 않는 한 같은 창에서 열려진 페이지는 모두 같은 session 객체를 공유
    	//setAttribute() 메소드를 사용해서 세션의 속성을 지정하게 되면 계속 상태를 유지
		List<Files> files = filesRepository.findAll();
	    Collections.shuffle(files);
		model.addAttribute("files", files);
    	
		return "index";
	}
	
	@RequestMapping("/chat")
	public void chat() {
	}

    @RequestMapping("/roompopup")
    public void roompopup() {
    }

	@GetMapping("/mypaging")
	public String mypaging(HttpServletRequest request, HttpServletResponse response,Model model,
			@RequestParam("username") String username,@RequestParam("nickname") String nickname) throws ParseException {
		user = new User();
		user = userRepository.findByUsername(username);

		files = new Files();
		files = filesRepository.findByUsername(username);
		
		DateFormat dateFormat = new SimpleDateFormat ("yyyy-MM-dd");
		Calendar today = Calendar.getInstance();
		Calendar d_day = Calendar.getInstance();
		d_day.setTime(dateFormat.parse(user.getRegdt()));
		long l_today = today.getTimeInMillis()/(24*60*60*1000);
		long l_d_day = d_day.getTimeInMillis()/(24*60*60*1000);
		
		request.getSession().setAttribute("pic", files.getFileurl()+files.getFilename());
		request.getSession().setAttribute("id", username);
		request.getSession().setAttribute("nickname", nickname);
		request.getSession().setAttribute("sex", user.getSex());
		request.getSession().setAttribute("birthday", user.getBirthday());
		request.getSession().setAttribute("email", user.getEmail());
		request.getSession().setAttribute("bio", files.getMessage());
		request.getSession().setAttribute("city", user.getCity());
		request.getSession().setAttribute("days", l_today-l_d_day);
		
		return "/mypage";
	}
	
    @RequestMapping("/mypage")
	public void mypage() {
	}

	@ResponseBody
	@RequestMapping(value="/mypagegetpic", method=RequestMethod.POST)
	public Map<String, Object> mypagegetpic(HttpServletRequest request, HttpServletResponse response,Model model,
			@RequestParam("username") String username) {
		files = new Files();
		files = filesRepository.findByUsername(username);
		Map<String, Object> userInfo = new HashMap<String, Object>();
		
		userInfo.put("pic", files.getFileurl()+files.getFilename());
		userInfo.put("bio", files.getMessage());
		
		return userInfo;
	}

	@GetMapping("/infopaging")
	public void infopaging(HttpServletRequest request, HttpServletResponse response,Model model,
			@RequestParam("username") String username) {
	    String personJson;
		
		user = new User();
		user = userRepository.findByUsername(username);

		files = new Files();
		files = filesRepository.findByUsername(username);
		
		if(user != null){
	        personJson = "{\"id\":\""+username
	                    +"\",\"sex\":\""+user.getSex()
	                    +"\",\"birthday\":\""+user.getBirthday()
	                    +"\",\"email\":\""+user.getEmail()
	                    +"\",\"picture\":\""+files.getFileurl()+files.getFilename()
	                    +"\",\"bio\":\""+files.getMessage()+"\"}";
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
	
	@ResponseBody
	@GetMapping("msgchange")
    public String msgchange(String message,String username) {
    	filesRepository.updateMessage(message,username);
    	files = filesRepository.findByUsername(username);
        
        return files.getMessage();
    }

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
			String fileUrl = "D:\\eclipse-workspace\\ttt\\src\\main\\resources\\static\\images\\"+username+"\\";

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

}
