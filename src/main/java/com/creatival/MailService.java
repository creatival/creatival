package com.creatival;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.creatival.team.TeamMember;
import com.creatival.user.Users;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    public void sendTestMail(String to) throws MessagingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper =
                new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject("Creatival 메일 테스트");

        String content = """
                <h2>메일 발송 테스트 성공 🎉</h2>
                <p>Spring Boot에서 Gmail SMTP 연결이 정상 동작합니다.</p>
                """;

        helper.setText(content, true);

        mailSender.send(message);
    }
    
    public void sendActiveUser(String to, String link) throws MessagingException {
    	MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper =
                new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject("creatival, 계정 활성화 메세지");

        helper.setText("계정활성화를 위해 아래 링크를 클릭하세요.\n"+link, true);

        mailSender.send(message);
    }
    
    public void deleteTeamMemberMail(String to, TeamMember teamMember ,String mailMessage) throws MessagingException {
    	MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper =
                new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(teamMember.getTeam().getName() + "팀 탈퇴처리 메세지");

        helper.setText(teamMember.getUser().getUsername() + "("+teamMember.getUser().getDisplayName()+") 님 \n"
        		+ "귀하에게 안타까운 메세지를 전하게되어 유감입니다. 소속되었던 팀인 " + teamMember.getTeam().getName() +"팀에서 "
        		+ teamMember.getUser().getUsername() + "("+teamMember.getUser().getDisplayName()+") 님께서 탈퇴처리되셨습니다. \n 사유는 다음과 같습니다.\n\n\n"
        		+ mailMessage);

        mailSender.send(message);
    }

	public void leaveTeamMemberMail(String to, TeamMember teamMember, String mailMessage) throws MessagingException {
		Users user = teamMember.getTeam().getUser();
		MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper =
                new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(teamMember.getTeam().getName() + "팀 탈퇴처리 메세지");

        helper.setText(user.getUsername() + "(" + user.getDisplayName() + ") 님 \n"
        		+ "귀하에게 안타까운 메세지를 전하게되어 유감입니다. 귀하의 팀인 " + teamMember.getTeam().getName() +"팀에서 "
        		+ teamMember.getUser().getUsername() + "("+teamMember.getUser().getDisplayName()+") 님께서 탈퇴하셨습니다. \n 사유는 다음과 같습니다. \n\n\n"
        		+ mailMessage);

        mailSender.send(message);
		
	}

	public void changeTeamLeader(String to, String link) throws MessagingException {
		MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper =
                new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject("팀장 변경 확인 메세지");

        helper.setText("팀장 변경을 동의하신다면 아래 링크를 클릭하세요.\n"+link, true);

        mailSender.send(message);
		
	}
}
