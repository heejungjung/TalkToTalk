package com.chat.talk.controller;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.chat.talk.model.Files;
import com.chat.talk.model.Mailer;
import com.chat.talk.services.SMTPAuthenticator;
import com.chat.talk.model.User;
import com.chat.talk.services.FilesService;
import com.chat.talk.services.UserService;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

@Controller
@RequestMapping("/account")
public class AccountController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private FilesService filesService;

    @GetMapping("/login")
    public String login() {
        return "account/login";
    }
    
    @GetMapping("/duplicated")
    public String duplicated() {
        return "account/duplicated";
    }
    
    @GetMapping("/loginProcess")
    public String loginProcess() {
        return "redirect:/";
    }
    
    @GetMapping("/loginSuccess")
    public String loginSuccess(HttpServletRequest request, User user) {
        return "/";
    }
    
    @GetMapping("/regist")
    public String regist() {
        return "account/regist";
    }

    @PostMapping("/regist")
    public String regist(User user, @RequestPart MultipartFile files) throws Exception{
    	String adr = user.getAddress();
    	System.out.println(adr);
    	user.setCity(adr.substring(0, adr.indexOf(" ")));
        userService.save(user);
        String username = user.getUsername();
		Files file = new Files();

		String sourceFileName = files.getOriginalFilename();
		//파일 첨부 여부
		if(sourceFileName != null && !sourceFileName.equals("")) {
			File destinationFile;
			String destinationFileName;
			String fileUrl = "D:\\eclipse-workspace\\ttt\\src\\main\\resources\\static\\images\\"+username+"\\";

			destinationFileName = profiledate() + "_" + sourceFileName;
			destinationFile = new File(fileUrl + destinationFileName);

			destinationFile.getParentFile().mkdirs();
			files.transferTo(destinationFile);

			//썸네일
			Thumbnails.of(fileUrl + destinationFileName).crop(Positions.CENTER).size(150, 150).toFile(new File(fileUrl,"s_"+destinationFileName));

			file.setFilename(destinationFileName);
			file.setRawname(sourceFileName);
			file.setFileurl("/images/"+username+"/");
		}else {
			file.setFileurl("/images/");
			if(user.getSex().equals("M")) {
				String[] pic = {"boy1.png", "boy2.png", "boy3.png"};
				double randomValue = Math.random();
		        int ran = (int)(randomValue * pic.length);
				String fname = pic[ran];
				System.out.println(ran);
				
				file.setFilename(fname);
			}else {
				String[] pic = {"girl1.png", "girl2.png", "girl3.png"};
				double randomValue = Math.random();
		        int ran = (int)(randomValue * pic.length);
				System.out.println(ran);
				String fname = pic[ran];
				
				file.setFilename(fname);
			}
		}
		file.setUsername(username);
		file.setNickname(user.getNickname());
		filesService.save(file,username);
		final String BODY = String.join(
                System.getProperty("line.separator"),
                "<div style='text-align:center;'>",
                "<h1><b>",
                username,
                "님💜</b> Talk To Talk 가입을 축하드립니다  😍 </h1>",
                "<p>우리 같이 꽃길🌸💮🌹🌺🌻🌼🌷만 걷자구용 🙉🙈🐾💕</p>",
                "</div>"
        		);
		SMTPAuthenticator smtp = new SMTPAuthenticator();
		Mailer mailer = new Mailer();
		mailer.sendMail(user.getEmail(),"💖 Welcome To Talk_To_Talk 💖", BODY, smtp);

        return "redirect:/";
    }

	private String profiledate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		String str = sdf.format(date);
		return str.replace("-","");
	}

	@ResponseBody
	@GetMapping("idCheck")
    public String id_check(String id) {
        String str = userService.idCheck(id);
        return str;
    }

	@ResponseBody
	@GetMapping("nnCheck")
    public String nn_check(String nickname) {
        String str = userService.nnCheck(nickname);
        return str;
    }

}