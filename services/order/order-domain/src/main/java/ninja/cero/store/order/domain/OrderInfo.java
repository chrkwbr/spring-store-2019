package ninja.cero.store.order.domain;

import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.StringJoiner;

public class OrderInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	public Long id;

	public String name;
	public String address;
	public String telephone;
	public String mailAddress;

	public String cardNumber;
	public String cardExpire;
	public String cardName;

	public String cartId;


	@Override
	public String toString() {
		return new StringJoiner(", ", OrderInfo.class.getSimpleName() + "[", "]")
			.add("id=" + id)
			.add("name='" + name + "'")
			.add("address='" + address + "'")
			.add("telephone='" + telephone + "'")
			.add("mailAddress='" + mailAddress + "'")
			.add("cardNumber='" + cardNumber + "'")
			.add("cardExpire='" + cardExpire + "'")
			.add("cardName='" + cardName + "'")
			.add("cartId='" + cartId + "'")
			.toString();
	}
}
