package ie.gmit.sw.db4o;


import com.db4o.*;
import com.db4o.config.*;
import com.db4o.query.*;
import com.db4o.ta.*;
import xtea_db4o.XTEA;
import xtea_db4o.XTeaEncryptionStorage;

import java.util.*;

import static java.lang.System.*;

public class OrderRunner {
	private ObjectContainer db = null;
	private List<Customer> customers = new ArrayList<Customer>();
	
	public OrderRunner() {
		init(); //Populate the customers collection
		
		EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
		config.common().add(new TransparentActivationSupport()); //Real lazy. Saves all the config commented out below
		config.common().add(new TransparentPersistenceSupport()); //Lazier still. Saves all the config commented out below
		config.common().updateDepth(7); //Propagate updates
		
		//Use the XTea lib for encryption. The basic Db4O container only has a Caesar cypher... Dicas quod non est ita!
		config.file().storage(new XTeaEncryptionStorage("password", XTEA.ITERATIONS64));
		
		/*
		config.common().objectClass(Patient.class).cascadeOnUpdate(true);
		config.common().objectClass(Patient.class).cascadeOnActivate(true);
		config.common().objectClass(MDTReview.class).cascadeOnUpdate(true);
		config.common().objectClass(MDTReview.class).cascadeOnActivate(true);
		config.common().objectClass(User.class).cascadeOnUpdate(true);
		config.common().objectClass(HospitalList.class).cascadeOnUpdate(true);
		config.common().objectClass(TumourSet.class).cascadeOnUpdate(true);
		config.common().objectClass(GPLetter.class).cascadeOnUpdate(true);
		*/

		//Open a local database. Use Db4o.openServer(config, server, port) for full client / server
		db = Db4oEmbedded.openFile(config, "customers.data");
		
		
		addCustomersToDatabase();
		showAllCustomers();
		getCustomerQBE(customers.get(0));
		getCustomerNative(customers.get(0));
		getCustomerSODA(customers.get(0));
		addLineitem("1", new Lineitem("QB-122", "Stoat Natural Whole Tail", 3, 4.50d));
	}
	
	/* Once we get a handle on an ObjectContainer, we are working 
	 * in a transactional environment. Adding objects to our database
	 * merely requires calling db.set(object). 
	 */
	private void addCustomersToDatabase(){
		for (Customer c: customers){
			db.store(c); //Adds the customer object to the database
		}
		db.commit(); //Commits the transaction
		//db.rollback(); //Rolls back the transaction
	}
	
	/* This method illustrates a simple Query By Example (QBE). Note the
	 * use of Customer.class as a parameter to the database query. This
	 * basically returns all Customer objects in the database.
	 */
	private void showAllCustomers(){
		//An ObjectSet is a specialised List for storing results
		ObjectSet<Customer> customers = db.query(Customer.class);
		for (Customer customer : customers) {
			out.println("[Customer] " + customer.getCustomerName() + "\t ***Database ObjID: " + db.ext().getID(customer));

			//Removing objects from the database is as easy as adding them
			//db.delete(customer);
			db.commit();
		}
	}
	
	/* The next three example illustrate the three methods of querying
	 * with the database:
	 * 1) QBE, where we pass prototypical instances to the query engine
	 * 2) Native Queries, where we pass queries written in an imperative programming style
	 * 3) SODA (Simple Object Data Access), 
	 * 
	 * While QBE queries are sufficient for simple example, there are some
	 * severe drawbacks using prototypical instances as the basis for
	 * filtering in a query. For this reason (and some others), native 
	 * queries are preferred.  
	 */
	private void getCustomerQBE(Customer c){
		//The new Customer(...) is a prototypical instance of the object(s) we want
		ObjectSet<Customer> customer = db.queryByExample(new Customer(c.getCustomerNumber(), c.getCustomerName(), c.getAddress()));
		if (customer.hasNext()) {
			out.println("[getCustomerQBE] found " + c.getCustomerName());
		} else {
			out.println("[Error] " + c.getCustomerNumber() + " is not in the database");
		}
	}
	
	/* Native queries are the preferred mechanism for querying an object
	 * database (or any database!) from an OOPL. Native queries work by
	 * constructing a predicate that evaluates to either true or false. If
	 * true, the matched object is added to the ObjectSet. Note that the 
	 * predicate is merely an anonymous inner class (recall lecture on
	 * classes & objects...!). Unlike other query languages like SQL,
	 * OQL, HQL etc.. the application of agile techniques such as 
	 * refactoring work seamlessly with native queries.   
	 * 
	 */
	
	// ******************  Best ************************
	private void getCustomerNative(final Customer c){
		ObjectSet<Customer> result = db.query(new Predicate<Customer>() {
			private static final long serialVersionUID = 777L;

			public boolean match(Customer customer) {
		        return customer.getCustomerNumber().equals(c.getCustomerNumber());
		    }	
		});
		
		if (result.hasNext()) {
			out.println("[getCustomerNative] found " + c.getCustomerName());
		} else {
			out.println("[Error] " + c.getCustomerNumber() + " is not in the database");
		}
	}

	private void getCustomerSODA(Customer c){
		Query query = db.query();
		query.constrain(Customer.class);
		query.descend("customerNumber").constrain(c.getCustomerNumber());
		ObjectSet<Customer> result = query.execute();
		if (result.hasNext()) {
			out.println("[getCustomerSODA] found " + c.getCustomerName());
		} else {
			out.println("[Error] " + c.getCustomerNumber() + " is not in the database");
		}
	}

	private void addLineitem(final String orderNumber, Lineitem item){
		ObjectSet<Order> result = db.query(new Predicate<Order>() {
			private static final long serialVersionUID = 777L;

			public boolean match(Order order) {
		        return order.getOrderNumber().equals(orderNumber);
		    }	
		});
		
		if (result.hasNext()) {
			Order o = result.next();
			out.println(o.size());
			
			o.add(item);
			db.store(o);
			db.commit();
			
			Iterator<Lineitem> i = o.iterator();
			while (i.hasNext()) {
				Lineitem line = i.next();
				out.println("\t[Lineitem]" + line.getPartDescription());
			}
			//out.println(o.size());
		} else {
			out.println("[Error] Order Number " + orderNumber + " is not in the database");
		}
		
	}
	
	private void init(){
		Address address1 = new Address("234 Main Street", "Salthill", County.Galway);
		Address address2 = new Address("11 Main Street", "Belmullet", County.Mayo);
		Address address3 = new Address("21 Main Street", "Waterford", County.Waterford);
		
		Customer c1 = new Customer("C1", "Sean Murphy", address1);
		Customer c2 = new Customer("C2", "Michael McGrath", address2);
		Customer c3 = new Customer("C3", "Mary Mannion", address3);
		
		customers.add(c1);
		customers.add(c2);
		customers.add(c3);
		
		Order order1 = new Order("1", new java.util.Date());
		Order order2 = new Order("2", new java.util.Date());
		Order order3 = new Order("3", new java.util.Date());
		Order order4 = new Order("4", new java.util.Date());
		Order order5 = new Order("5", new java.util.Date());
		Order order6 = new Order("6", new java.util.Date());
		
		c1.add(order1);
		c1.add(order2);
		c2.add(order3);
		c2.add(order4);
		c3.add(order5);
		c3.add(order6);
		
		Lineitem l1 = new Lineitem("QB-101", "333 Classic Trout All Purpose Fly Line", 8, 35.99d);
		Lineitem l2 = new Lineitem("QB-102", "Diawa M-ONE PLUS Salmon Mooching & Trolling Reel", 12, 87.99d);
		Lineitem l3 = new Lineitem("QB-103", "Diawa Trout Fly Rod", 4, 44.99d);
		Lineitem l4 = new Lineitem("QB-104", "Diawa Lexa Salmon Fly Rod", 3, 442.00d);
		Lineitem l5 = new Lineitem("QB-105", "Odyssey Salmon Fly Rod", 13, 65.24d);
		Lineitem l6 = new Lineitem("QB-106", "Bruce and Walker Norway Speycaster Salmon Fly Rod", 1, 610.35d);
		Lineitem l7 = new Lineitem("QB-107", "Sage One Fly Rod", 1, 789.10d);
		Lineitem l8 = new Lineitem("QB-108", "Mitchell Mag-Pro Extreme 2000 Reel ", 1, 187.99d);
		Lineitem l9 = new Lineitem("QB-109", "Abu Garcia Multiplier Reel - Ambassadeur 6500 CS Chrome Rocket", 1, 158.00d);
		Lineitem l10 = new Lineitem("QB-110", "Hardy Shadow 4pc Fly Fishing Rod 10' #7", 1, 318.59d);
		Lineitem l11 = new Lineitem("QB-111", "Hardy Ultralite DD Black Edition Fly Reel", 1, 239.26d);
		Lineitem l12 = new Lineitem("QB-112", "Shakespeare Sigma 3/4 Fly Reel", 8, 35.99d);
		Lineitem l13 = new Lineitem("QB-113", "Shakespeare Oracle 10/11 Salmon Fly Reel", 2, 97.65d);
		Lineitem l14 = new Lineitem("QB-114", "Golden Pheasant Topping Crest (Dyed)", 3, 8.20d);
		Lineitem l15 = new Lineitem("QB-115", "Golden Pheasant Complete Tail", 7, 10.00d);
		
		order1.add(l1);
		order1.add(l2);
		order1.add(l3);
		order2.add(l4);
		order2.add(l5);
		order2.add(l6);
		order3.add(l7);
		order3.add(l8);
		order4.add(l9);
		order4.add(l10);
		order5.add(l11);
		order5.add(l12);
		order6.add(l13);
		order6.add(l14);
		order6.add(l15);
	}

	public static void main(String[] args) {
		new OrderRunner();
	}
}
