import com.orientechnologies.orient.core.db.*;
import com.orientechnologies.orient.core.metadata.schema.*;
import com.orientechnologies.orient.core.record.*;
import com.orientechnologies.orient.core.sql.executor.*;

import java.util.*;

public class Main {
	public static void main(String[] args) {
		OrientDB orient = new OrientDB("remote:localhost", OrientDBConfig.defaultConfig());
		ODatabaseSession db = orient.open("test", "root", "heslo");
		
		createSchema(db);
		truncate(db);
		createPeople(db);
		executeAQuery(db);
		executeAnotherQuery(db);
		
		db.close();
		orient.close();
	}
	
	private static void createSchema(ODatabaseSession db) {
		OClass person = db.getClass("Person");
		
		if(person == null) {
			person = db.createVertexClass("Person");
		}
		
		if(person.getProperty("name") == null) {
			person.createProperty("name", OType.STRING);
			person.createIndex("Person_name_index", OClass.INDEX_TYPE.NOTUNIQUE, "name");
		}
		
		if(db.getClass("FriendOf") == null) {
			db.createEdgeClass("FriendOf");
		}
	}
	
	private static void createPeople(ODatabaseSession db) {
		OVertex alice = createPerson(db, "Alice", "Foo");
		OVertex bob = createPerson(db, "Bob", "Bar");
		OVertex jim = createPerson(db, "Jim", "Baz");
		
		OEdge edge1 = alice.addEdge(bob, "FriendOf");
		edge1.save();
		OEdge edge2 = bob.addEdge(jim, "FriendOf");
		edge2.save();
	}
	
	private static OVertex createPerson(ODatabaseSession db, String name, String surname) {
		OVertex result = db.newVertex("Person");
		result.setProperty("name", name);
		result.setProperty("surname", surname);
		result.save();
		return result;
	}
	
	private static void executeAQuery(ODatabaseSession db) {
		String query = "SELECT expand(out('FriendOf').out('FriendOf')) from Person where name = ?";
		OResultSet rs = db.query(query, "Alice");
		
		while(rs.hasNext()) {
			OResult item = rs.next();
			System.out.println("friend: " + item.getProperty("name"));
		}
		
		rs.close(); //REMEMBER TO ALWAYS CLOSE THE RESULT SET!!!
	}
	
	private static void executeAnotherQuery(ODatabaseSession db) {
		String query =
			" MATCH                                           " +
				"   {class:Person, as:a, where: (name = :name1)}, " +
				"   {class:Person, as:b, where: (name = :name2)}, " +
				"   {as:a} -FriendOf-> {as:x} -FriendOf-> {as:b}  " +
				" RETURN x.name as friend                         ";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name1", "Alice");
		params.put("name2", "Jim");
		
		OResultSet rs = db.query(query, params);
		
		while(rs.hasNext()) {
			OResult item = rs.next();
			System.out.println("friend: " + item.getProperty("friend"));
		}
		
		rs.close();
	}
	
	private static void truncate(ODatabaseSession db) {
		String query = "TRUNCATE CLASS `Person` unsafe";
		db.command(query);
		System.out.println("Database cleaned via truncate command!");
	}
}
