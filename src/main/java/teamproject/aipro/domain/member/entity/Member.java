package teamproject.aipro.domain.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Member {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String userid;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private String username;

	public Member(String userid, String password, String username) {
		this.userid = userid;
		this.password = password;
		this.username = username;
	}
}
