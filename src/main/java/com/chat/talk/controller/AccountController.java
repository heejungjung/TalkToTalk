package com.chat.talk.controller;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.chat.talk.model.Files;
import com.chat.talk.model.Mailer;
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

    //환경변수 사용
    @Value("${dir}")
    private String dir;
    
    @GetMapping("/login")
    public String login() {
        return "account/login";
    }
    
    //중복 로그인 시
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

    //회원가입 후 DB에 저장
    @PostMapping("/regist")
    public String regist(User user, @RequestPart MultipartFile files) throws Exception{
    	String adr = user.getAddress();
    	user.setCity(adr.substring(0, adr.indexOf(" ")));
        userService.save(user);
        String username = user.getUsername();
		Files file = new Files();

		String sourceFileName = files.getOriginalFilename();
		//파일 첨부 여부
		if(sourceFileName != null && !sourceFileName.equals("")) {
			File destinationFile;
			String destinationFileName;
			String fileUrl = dir+username+"/";

			destinationFileName = profiledate() + "_" + sourceFileName;
			destinationFile = new File(fileUrl + destinationFileName);

			destinationFile.getParentFile().mkdirs();
			files.transferTo(destinationFile);

			//썸네일
			Thumbnails.of(fileUrl + destinationFileName).crop(Positions.CENTER).size(150, 150).toFile(new File(fileUrl,"s_"+destinationFileName));

			file.setFilename(destinationFileName);
			file.setRawname(sourceFileName);
			file.setFileurl("/images/"+username+"/");
		}else { //프로필 사진 미등록시 남여 성별에 따라 기본 캐릭터 이미지로 설정
			file.setFileurl("/images/");
			if(user.getSex().equals("M")) {
				String[] pic = {"boy1.png", "boy2.png", "boy3.png"};
				double randomValue = Math.random();
		        int ran = (int)(randomValue * pic.length);
				String fname = pic[ran];
				
				file.setFilename(fname);
			}else {
				String[] pic = {"girl1.png", "girl2.png", "girl3.png"};
				double randomValue = Math.random();
		        int ran = (int)(randomValue * pic.length);
				String fname = pic[ran];
				
				file.setFilename(fname);
			}
		}
		file.setUsername(username);
		file.setNickname(user.getNickname());
		filesService.save(file);
		
		//회원가입 시 등록된 이메일로 회원가입 축하 메일 전송
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

    //사진 저장을 위한 날짜 설정
	private String profiledate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		String str = sdf.format(date);
		return str.replace("-","");
	}

	//회원가입 시 아이디 중복 체크
	@ResponseBody
	@GetMapping("idCheck")
    public String id_check(String id) {
        String str = userService.idCheck(id);
        return str;
    }

	//회원가입 시 닉네임 중복 체크
	@ResponseBody
	@GetMapping("nnCheck")
    public String nn_check(String nickname) {
        String str = userService.nnCheck(nickname);
        return str;
    }

}