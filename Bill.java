import java.util.*;
import java.sql.*;
public class Bill
{
	private int cid;
	private String cname;
	private String date;
	private int sid;
	static String query;
	static Scanner sc=new Scanner(System.in);
	Bill(int cid,String date,int sid)
	{
		this.cid=cid;
		this.date=date;
		this.sid=sid;
	}
	void startAddBillItems(Connection con)throws Exception
	{
		Statement st=con.createStatement();
		ResultSet rs;
		String query;
		int sum=0;
		query=String.format("select name from billings.customers where cid=%d",cid);
		rs=st.executeQuery(query);
		rs.next();
		cname=rs.getString("name");
		query="insert into billings.bills (cusid,cusname,storeid,date) values (?,?,?,?)";
		PreparedStatement ps=con.prepareStatement(query);
		ps.setInt(1,cid);
		ps.setString(2,cname);
		ps.setInt(3,sid);
		ps.setString(4,date);
		ps.executeUpdate();
		query=String.format("select billid from billings.bills order by billid desc limit 1");
		rs=st.executeQuery(query);
		rs.next();
		int bid=rs.getInt("billid");
		System.out.println("how many kind of items do you want to add: ");
		int n=sc.nextInt();
		boolean flag=false;
		for(int i=0;i<n;i++)
		{
			System.out.println("enter item id: ");
			int itemid=sc.nextInt();
			if(Item.itemExist(con,itemid))
			{
				query=String.format("select itemname,itemprice from billings.items where itemid=%d",itemid);
				rs=st.executeQuery(query);
				rs.next();
				String iname=rs.getString("itemname");
				int iprice=rs.getInt("itemprice");
				System.out.println("how many quantity of "+iname+" do you want? ");
				int q=sc.nextInt();
				Billitem bi=new Billitem(bid,itemid,iname,iprice,q,sid);
				sum+=bi.AddtoBillItemstoDB(con);
				flag=true;
			}
			else
				System.out.println("item doesnot exists!");
		}
		if(flag)
		{
			query=String.format("update billings.bills set amount=%d where billid=%d",sum,bid);
			st.executeUpdate(query);
			query=String.format("update billings.stores set revenue=revenue+%d where storeid=%d",sum,sid);
			st.executeUpdate(query);
			System.out.println("bill created successfully ");
		}
		else
			System.out.println("can't create bill");
		
	}
	static void showAllBills(Connection con) throws Exception
	{
		String query;
		ResultSet rs;
		query="select bills.billid,bills.cusid,bills.cusname,bills.date,billitems.itemid,billitems.itemname,billitems.itemprice,billitems.quantity, billitems.value from bills right join billitems on bills.billid=billitems.billid";
		Statement st=con.createStatement();
		rs=st.executeQuery(query);
		System.out.println("BILL ID : CUSTOMER ID : CUSTOMER NAME : BILL DATE : ITEM ID : ITEM PRICE : QUANTITY : VALUE");
		while(rs.next())
		{
			System.out.println(rs.getInt("bills.billid")+"          "+rs.getInt("bills.cusid")+"          "+rs.getString("cusname")+"    "+rs.getString("date")+"        " +rs.getInt("itemid")+",           "+rs.getString("itemname")+" ,      "+rs.getString("itemprice")+" , "+rs.getInt("quantity")+" ,   "+rs.getInt("value"));
		}
	}
	static void showBill(int bid,Connection con) throws Exception
	{
		ResultSet rs;
		Statement st;
		int sum=0;
		st=con.createStatement();
		query=String.format("select * from billings.bills where billid=%d",bid);
		rs=st.executeQuery(query);
		System.out.println("                     bill details    ");
		rs.next();
		System.out.println("BILLID: "+rs.getInt("billid")+"  , STOREID: "+rs.getInt("storeid")+"CUSTOMER ID: "+rs.getInt("cusid")+" ,  "+"CUSTOMER NAME:  "+rs.getString("cusname")+"  ,  "+"BILL DATE: "+rs.getString("date"));
		query=String.format("select bills.billid, bills.cusid, bills.cusname ,bills.date,billitems.itemid,billitems.itemname,billitems.itemprice,billitems.quantity, billitems.value from bills inner join billitems on bills.billid=billitems.billid and bills.billid=%d",bid);
		rs=st.executeQuery(query);
		System.out.println(" ITEMID : ITEMNAME : ITEMPRICE : QUANTITY : VALUE ");
		while(rs.next())
		{
			sum+=rs.getInt("value");
			System.out.println("    " +rs.getInt("itemid")+",     "+rs.getString("itemname")+" ,      "+rs.getString("itemprice")+" , "+rs.getInt("quantity")+" ,   "+rs.getInt("value"));
		}
		System.out.println("                            TOTAL SUM ="+sum);
	}
	static void deleteBill(int bid,Connection con) throws Exception
	{
		query=String.format("delete from billings.bills where billid=%d",bid);
		Statement st=con.createStatement();
		if(st.executeUpdate(query)>0)
			System.out.println("bill deleted sucessfully!");
	}
	static boolean billExists(int bid,Connection con) throws Exception
	{
		boolean flag=false;
		Statement st=con.createStatement();
	    query=String.format("select * from billings.bills where billid=%d",bid);
	    ResultSet rs=st.executeQuery(query);
	    while(rs.next())
	    {
	    	flag=true;
	    	break;
	    }
	    if(flag)
	    	return true;
	    else 
	    	return false;
	}
}

