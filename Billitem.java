import java.sql.*;
//import java.util.*;
public class Billitem 
{
	private int billid;
	private int itemid;
	private String itemname;
	private int itemprice;
	private int quantity;
	private int sid;
	Billitem(int billid,int itemid,String itemname,int itemprice,int quantity,int sid)
	{
		this.billid=billid;
		this.itemid=itemid;
		this.itemname=itemname;
		this.itemprice=itemprice;
		this.quantity=quantity;
		this.sid=sid;
	}
	int AddtoBillItemstoDB(Connection con) throws Exception
	{
		String query="insert into billings.billitems  (billid,itemid,storeid,itemname,itemprice,quantity) values (?,?,?,?,?,?)";
		PreparedStatement st=con.prepareStatement(query);
		st.setInt(1, billid);
		st.setInt(2, itemid);
		st.setInt(3,sid);
		st.setString(4,itemname);
		st.setInt(5,itemprice);
		st.setInt(6, quantity);
		st.executeUpdate();
		st.close();
		Statement stm=con.createStatement();
		query=String.format("update billings.storeitems set stock=stock-%d where itemid=%d and storeid=%d",quantity,itemid,sid);
		//query=String.format("update billings.items set itemstock=itemstock-%d where itemid=%d",quantity,itemid);
		stm.executeUpdate(query);
		stm.close();
		return itemprice*quantity;
	}
	
}
