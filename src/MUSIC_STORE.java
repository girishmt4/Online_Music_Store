import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
import java.util.Scanner;

public class MUSIC_STORE {
	public static void main(String[] args) {
		Connection connection = null;
        try {
        	Class.forName("org.sqlite.JDBC");
        	connection = DriverManager.getConnection("jdbc:sqlite:chinook.db");
        	System.out.println("Opened database successfully");
        	String repeatChoice;
        	Scanner scanner=new Scanner(System.in);
        	Statement statement=connection.createStatement();
        	Statement statement2=connection.createStatement();
        	do {
        		System.out.println("Select a choice from below : \n");
            	System.out.println("1. Obtain album title(s) based on artist name");
            	System.out.println("2. Display Tracks of a Album Title");
            	System.out.println("3. Purchase History for a customer");
            	System.out.println("4. Update track price(Individual)");
            	System.out.println("5. Update track price(Batch)");
            	System.out.println("6. Identify Marketable Populaion and Material");
            	System.out.println("7. Simple Track Recommender");
            	System.out.println("8. Top Sellers by Revenue");
            	System.out.println("9. Top Sellers by Volume");
            	System.out.println("10. Exit");
            	int choice=scanner.nextInt();
            	String query;
            	switch(choice) {
            		//Obtain album title(s) based on artist name
            		case 1:{
            			System.out.println("Please Enter the artist name : ");
            			String artist=scanner.next();
            			query="select count(Al.AlbumId) from Album Al, Artist Ar where Al.ArtistId=Ar.ArtistId AND Ar.Name like '%"+artist+"%';";
            			ResultSet resultSet=statement.executeQuery(query);
            			int count=resultSet.getInt("count(Al.AlbumId)");
            			if(count==0) {
            				System.out.println("There are no Album Titles listed under the artist you specified!\n");
            			}
            			else {
            				query="select Al.AlbumId, Al.Title, Ar.ArtistId from Album Al, Artist Ar where Al.ArtistId=Ar.ArtistId AND Ar.Name like '%"+artist+"%';";
            				resultSet=statement.executeQuery(query);
            				int tempArtistId=0;
            				while(resultSet.next()) {
            					int albumId=resultSet.getInt("AlbumId");
            					String albumTitle=resultSet.getString("Title");
            					int artistId=resultSet.getInt("ArtistId");
            					if(artistId!=tempArtistId) {
            						System.out.println("---------------------Artist ID : "+artistId+"-------------------------");
            						tempArtistId=artistId;
            					}
            					System.out.println("\nAlbum ID : "+albumId);
                				System.out.println("Album Title : "+albumTitle);
            				}
            			}
            			break;
            		}
            		//Obtain tracks of album title
            		case 2:{
            			System.out.println("Enter the Album Title : ");
            			String albumTitle = scanner.next();
            			albumTitle += scanner.nextLine();		
            			query="select count(T.TrackId) from Track T, Genre G, Album A where A.AlbumId=T.AlbumId AND T.GenreId=G.GenreId AND A.Title like '%"+albumTitle+"%';";
            			
            			ResultSet resultSet=statement.executeQuery(query);
            			int count=resultSet.getInt("count(T.TrackId)");
            			int[] arrayTrackId=new int[count];
            			int iterator=0;
            			if(count==0) {
            				System.out.println("There is no record of the album you specified!\n");
            			}
            			else {
            				query="select A.AlbumId, T.TrackId, T.Name as TrackName, G.Name as GenreName, T.UnitPrice from Track T, Genre G, Album A where A.AlbumId=T.AlbumId AND T.GenreId=G.GenreId AND A.Title like '%"+albumTitle+"%';";
            				resultSet=statement.executeQuery(query);
            				System.out.println("Detailed Tracks of The Album: ");
            				int tempAlbumId=0;
            				while(resultSet.next()) {
            					int trackId=resultSet.getInt("TrackId");
            					String trackName=resultSet.getString("TrackName");
            					String genreName=resultSet.getString("GenreName");
            					Float unitPrice=resultSet.getFloat("UnitPrice");
            					int albumId=resultSet.getInt("AlbumId");
            					if(albumId!=tempAlbumId) {
            						System.out.println("---------------------Album ID : "+albumId+"-------------------------");
            						tempAlbumId=albumId;
            					}
            					System.out.println("\nTrack ID : "+trackId);
            					System.out.println("Track Name : "+trackName);
            					System.out.println("Genre Name : "+genreName);
            					System.out.println("Unit Price : "+unitPrice);
            					arrayTrackId[iterator]=trackId;
            					iterator++;
            				}
            				System.out.println("Do you want to purchase one of the tracks?(Y/N)");
            				String answer=scanner.next();
            				if(answer.equals("Y")||answer.equals("y")) {
            					System.out.println("Enter the track ID that you want to purchase : ");
            					int purchaseTrackId=scanner.nextInt();
            					boolean validTrackId=false;
            					for(iterator=0;iterator<arrayTrackId.length;iterator++) {
            						if(purchaseTrackId==arrayTrackId[iterator]) {
            							validTrackId=true;
            							break;
            						}
            					}
            					if(validTrackId==true) {
            						System.out.println("Enter quantity : ");
                					int purchaseQuantity=scanner.nextInt();
                					if(purchaseQuantity>0) {
                						//Update the tables
                						System.out.println("All Well!");
                						query="select Address, City, State, Country, PostalCode from Customer where CustomerId=25;";
                						resultSet=statement.executeQuery(query);
                						String address=resultSet.getString("Address");
                						String city=resultSet.getString("City");
                						String state=resultSet.getString("State");
                						String country=resultSet.getString("Country");
                						long postalCode=resultSet.getLong("PostalCode");
                						//DateTime date=new Date();
                						query="select max(InvoiceId) as MaxInvoiceId from Invoice";
                						resultSet=statement.executeQuery(query);
                						int invoiceId=resultSet.getInt("MaxInvoiceId")+1;
                						query="select UnitPrice from Track where TrackId="+purchaseTrackId;
                						resultSet=statement.executeQuery(query);
                						float unitPrice=resultSet.getFloat("UnitPrice");
                						float totalPrice=purchaseQuantity*unitPrice;
                						System.out.println(invoiceId);
                						System.out.println(address);
                						System.out.println(city);
                						System.out.println(state);
                						System.out.println(country);
                						System.out.println(postalCode);
                						System.out.println(invoiceId);
                						System.out.println(totalPrice);
                						query="insert into Invoice(InvoiceId,CustomerId,InvoiceDate,BillingAddress,BillingCity,BillingState,BillingCountry,BillingPostalCode,Total) values ("+invoiceId+","+25+",datetime('now','localtime'),'"+address+"','"+city+"','"+state+"','"+country+"',"+postalCode+","+totalPrice+");";
                						statement.executeUpdate(query);
                						
                						query="select max(InvoiceLineId) as MaxInvoiceLineId from InvoiceLine";
                						resultSet=statement.executeQuery(query);
                						
                						int invoiceLineId=resultSet.getInt("MaxInvoiceLineId")+1;
                						query="insert into InvoiceLine(InvoiceLineId,InvoiceId,TrackId,UnitPrice,Quantity) values("+invoiceLineId+","+invoiceId+","+purchaseTrackId+","+unitPrice+","+purchaseQuantity+");";
                						statement.executeUpdate(query);
                						
                					}
                					else {
                						System.out.println("Invalid Quantity : Quantity can not be "+purchaseQuantity);
                					}
            					}
            					else {
            						System.out.println("TrackId invalid : We could not find the TrackId "+purchaseTrackId+" in your search");
            					}
            				}
            			}
            			break;
            		}
            		//Purchase history for a customer
            		case 3:{
            			System.out.println("Please Enter the customer ID : ");
            			int customerId=scanner.nextInt();
            			query="select count(L.TrackId) from Invoice I, InvoiceLine L where I.CustomerId="+customerId+" AND I.InvoiceId=L.InvoiceId;";
            			ResultSet resultSet=statement.executeQuery(query);
            			int count=resultSet.getInt("count(L.TrackId)");
            			if(count==0) {
            				System.out.println("There is no record of the customer ID you have specified!\n");
            			}
            			else {
            				query="select I.InvoiceDate, T.Name, A.Title, L.Quantity from Invoice I, InvoiceLine L, Track T, Album A where I.CustomerId="+customerId+" AND I.InvoiceId=L.InvoiceId AND L.TrackId=T.TrackId AND A.AlbumId=T.AlbumId";
            				resultSet=statement.executeQuery(query);
            				System.out.println("Detailed History of the customer: ");
            				while(resultSet.next()) {
                				String trackName=resultSet.getString("Name");
                				String albumTitle=resultSet.getString("Title");
                				String quantity=resultSet.getString("Quantity");
                				String invoiceDate=resultSet.getString("InvoiceDate");
                				System.out.println("\nTrack Name : "+trackName);
                				System.out.println("Album Title : "+albumTitle);
                				System.out.println("Quantity : "+quantity);
                				System.out.println("Invoice Date : "+invoiceDate);
                			}
            			}
            			break;
            		}
            		//Update track price (individual)
            		case 4:{
            			System.out.println("Please Enter the track ID : ");
            			int trackId=scanner.nextInt();
            			
            			query="select count(TrackId) from Track where TrackId="+trackId+";";
            			ResultSet resultSet=statement.executeQuery(query);
            			int count=resultSet.getInt("count(TrackId)");
            			if(count==0) {
            				System.out.println("There is no record of the customer ID you have specified!\n");
            			}
            			else {
            				query="select UnitPrice from Track where TrackId="+trackId+";";
                			resultSet=statement.executeQuery(query);
                			float originalPrice=resultSet.getFloat("UnitPrice");
                			System.out.println("The current price is : "+originalPrice);
                			System.out.println("Enter the price to be updated : ");
                			float updatedPrice=scanner.nextFloat();
                			query="update Track set UnitPrice="+updatedPrice+" where TrackId="+trackId+";";
                			statement.executeUpdate(query);
                			query="select UnitPrice from Track where TrackId="+trackId+";";
                			resultSet=statement.executeQuery(query);
                			float tryPrice=resultSet.getFloat("UnitPrice");
                			System.out.println("The Price successfully changed! The price is now "+tryPrice);
            			}
            			break;
            		}
            		//Update track price(Batch)
            		case 5:{
            			System.out.println("Enter the percentage value : ");
            			float percentage=scanner.nextInt();
            			if(percentage<-100||percentage>100) {
            				System.out.println("Invalid percentage!");
            			}
            			else {
            				float multiplier=1+(percentage/100);
            				System.out.println(multiplier);
            				query="select TrackId,Name,UnitPrice from Track";
            				ResultSet resultSet=statement.executeQuery(query);
            				while(resultSet.next()) {
            					int trackId=resultSet.getInt("TrackId");
            					String name=resultSet.getString("Name");
            					float previousUnitPrice=resultSet.getFloat("UnitPrice");
            					float updatedUnitPrice=previousUnitPrice*multiplier;
            					System.out.println("\nTrack ID: "+trackId);
            					System.out.println("Name: "+name);
            					System.out.println("Previous Unit Price: "+previousUnitPrice);
            					System.out.println("Updated Unit Price: "+updatedUnitPrice);
            					query="update Track set UnitPrice="+updatedUnitPrice+" where TrackId="+trackId;
            					statement2.executeUpdate(query);
            				}
            			}
            			break;
            		}
            		
            		case 6:{
            			System.out.println("Please Enter the state : ");
            			String enteredState=scanner.next();
            			query="select count(*) from InvoiceLine L,Invoice I where I.InvoiceId=L.InvoiceId and BillingState='"+enteredState+"';";
            			ResultSet resultSet=statement.executeQuery(query);
            			int count=resultSet.getInt("count(*)");
            			if(count==0){
            				System.out.println("Please enter correct input");
            			}
            			else {
            				query="select A.AlbumId, A.Title, count(T.TrackId) from Album A, Track T where A.AlbumId=T.AlbumId and T.TrackId not in(select L.TrackId from InvoiceLine L, Invoice I where L.InvoiceId=I.InvoiceId and I.BillingState='"+enteredState+"') group by A.AlbumId;";
                			resultSet=statement.executeQuery(query);
                			System.out.println("--------------------------Marketable Material");
                			while(resultSet.next()) {
                				int albumId=resultSet.getInt("AlbumId");
                				String albumTitle=resultSet.getString("Title");
                				System.out.println("\nAlbum ID : "+albumId);
                				System.out.println("Album Title : "+albumTitle);
                			}
                			query="select CustomerId, FirstName, LastName, Company, Address, City, State, Country, PostalCode, Phone, Fax, Email, SupportRepId from Customer where State='"+enteredState+"';";
                			resultSet=statement.executeQuery(query);
                			System.out.println("--------------------------Marketable Population");
                			while(resultSet.next()) {
                				int customerId=resultSet.getInt("CustomerId");
                				String firstName=resultSet.getString("FirstName");
                				String lastName=resultSet.getString("LastName");
                				String company=resultSet.getString("Company");
                				String address=resultSet.getString("Address");
                				String city=resultSet.getString("City");
                				String state=resultSet.getString("State");
                				String country=resultSet.getString("Country");
                				String postalCode=resultSet.getString("PostalCode");
                				String phone=resultSet.getString("Phone");
                				String fax=resultSet.getString("Fax");
                				String email=resultSet.getString("Email");
                				int supportRepId=resultSet.getInt("SupportRepId");
                				System.out.println("\nCustomer ID : "+customerId);
                				System.out.println("First Name : "+firstName);
                				System.out.println("Last Name : "+lastName);
                				System.out.println("Company : "+company);
                				System.out.println("Address : "+address);
                				System.out.println("City : "+city);
                				System.out.println("State : "+state);
                				System.out.println("Country : "+country);
                				System.out.println("Postal Code : "+postalCode);
                				System.out.println("Phone : "+phone);
                				System.out.println("Fax : "+fax);
                				System.out.println("Email : "+email);
                				System.out.println("Support Rep ID : "+supportRepId);
                			}
            			}
            			
            			break;
            			}
            		//Simple Track Recommender
            		case 7:{
            			System.out.println("Enter the customer ID : ");
            			int enteredCustomerId=scanner.nextInt();
            			query="select count(*) from Customer where CustomerId="+enteredCustomerId+";";
            			ResultSet resultSet=statement.executeQuery(query);
            			int count=resultSet.getInt("count(*)");
            			if(count==0) {
            				System.out.println("Please enter correct customer ID");
            			}
            			else {
            				query="select count(*) from (select A.AlbumId, A.ArtistId, T.TrackId, I.CustomerId from Album A, Track T, InvoiceLine L, Invoice I where I.InvoiceId=L.InvoiceId and L.TrackId=T.TrackId and T.AlbumId=A.AlbumId and I.CustomerId="+enteredCustomerId+" group by I.CustomerId,A.AlbumId having count(*)>=3)";
                			resultSet=statement.executeQuery(query);
                			count=resultSet.getInt("count(*)");
                			int[] albumArray=new int[count];
                			query="select A.AlbumId from Album A, Track T, InvoiceLine L, Invoice I where I.InvoiceId=L.InvoiceId and L.TrackId=T.TrackId and T.AlbumId=A.AlbumId and I.CustomerId="+enteredCustomerId+" group by I.CustomerId,A.AlbumId having count(*)>=3;";
                			resultSet=statement.executeQuery(query);
                			int i=0;
                			while(resultSet.next()) {
                				albumArray[i]=resultSet.getInt("AlbumId");
                				i++;
                			}
                			for(i=0;i<count;i++) {
                				System.out.println(albumArray[i]);
                			}
                			System.out.println("--------------------------Track Recommendations");
                			for(i=0;i<count;i++) {
                				query="select T.TrackId, T.Name, T.AlbumId, A.Title from Track T, Album A where A.AlbumId=T.AlbumId and T.AlbumId="+albumArray[i]+" and T.TrackId not in(select L.TrackId from InvoiceLine L, Invoice I, Track T, Album A where I.InvoiceId=L.InvoiceId and I.CustomerId="+enteredCustomerId+" and L.TrackId=T.TrackId and T.AlbumId="+albumArray[i]+" group by T.TrackId);";
                				resultSet=statement.executeQuery(query);
                				while(resultSet.next()) {
                					int trackId=resultSet.getInt("TrackId");
                					String trackName=resultSet.getString("Name");
                					int albumId=resultSet.getInt("AlbumId");
                					String albumTitle=resultSet.getString("Title");
                					System.out.println("\nTrack ID : "+trackId);
                					System.out.println("Track Name : "+trackName);
                					System.out.println("Album ID : "+albumId);
                					System.out.println("Album Title : "+albumTitle);
                				}
                			}
            			}
            			
            			
            			break;
            			}
            		//Top Sellers By Revenue
            		case 8:{
            			query="select max(revenue) from(select sum(L.UnitPrice*L.Quantity) as revenue from InvoiceLine L, Album A, Track T where L.TrackId=T.TrackId and T.AlbumId=A.AlbumId group by A.ArtistId);";
            			ResultSet resultSet=statement.executeQuery(query);
            			double maxRevenue=resultSet.getDouble("max(revenue)");
            			query="select R.ArtistId, R.Name from InvoiceLine L, Album A, Track T,Artist R where R.ArtistId=A.ArtistId and L.TrackId=T.TrackId and T.AlbumId=A.AlbumId group by A.ArtistId having sum(L.UnitPrice*L.Quantity)="+maxRevenue+";";
            			resultSet=statement.executeQuery(query);
            			System.out.println("Artist(s) generating highest sales revenue : ");
            			while(resultSet.next()) {
            				int artistId=resultSet.getInt("ArtistId");
            				String artistName=resultSet.getString("Name");
            				System.out.println("\nArtist ID : "+artistId);
            				System.out.println("Artist Name : "+artistName);
            			}
            			break;
            			}
            		//Top Sellers By Volume
            		case 9:{
            			query="select max(tracksCount) from (select count(T.TrackId) as tracksCount from Album A, Track T, InvoiceLine L where A.AlbumId=T.AlbumId and T.TrackId=L.TrackId group by A.ArtistId);";
            			ResultSet resultSet=statement.executeQuery(query);
            			double maxCount=resultSet.getDouble("max(tracksCount)");
            			query="select R.ArtistId, R.Name from Album A, Track T, InvoiceLine L, Artist R where R.ArtistId=A.ArtistId and A.AlbumId=T.AlbumId and T.TrackId=L.TrackId group by A.ArtistId having count(T.TrackId)="+maxCount+";";
            			resultSet=statement.executeQuery(query);
            			System.out.println("Artist(s) whose tracks are generating highest sales volume : ");
            			while(resultSet.next()) {
            				int artistId=resultSet.getInt("ArtistId");
            				String artistName=resultSet.getString("Name");
            				System.out.println("\nArtist ID : "+artistId);
            				System.out.println("Artist Name : "+artistName);
            			}
            			break;
            			}
            		
            		case 10:{
            			System.exit(0);
            		}
            		default:{
            			System.out.println("Please Enter Correct Choice! ");
            		}
            	}
            	System.out.println("Do You Want To Continue? (Y/N)");
            	repeatChoice=scanner.next();
        	}while(repeatChoice.equals("Y")||repeatChoice.equals("y"));
        	
        	scanner.close();
        	statement.close();
        	connection.close();
        } catch (ClassNotFoundException e) {
        	e.printStackTrace();
        } catch (SQLException e) {
        	e.printStackTrace();
        }
	}
}