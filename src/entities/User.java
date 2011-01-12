package entities;

public class User {
	public String principal;
	public int votesLeft;

	public User(String principal, int votesLeft) {
		this.principal = principal;
		this.votesLeft = votesLeft;
	}
}
