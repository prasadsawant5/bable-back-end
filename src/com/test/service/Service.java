package com.test.service;

import java.awt.datatransfer.StringSelection;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.Cursor;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.sun.research.ws.wadl.Response;

@Path("/service")
public class Service {

	private static final String COLLECTIONNAME = "user_information";
	private MongoClientURI mongoClientUri;
	private MongoClient mongoClient;
	private DB db;
	private DBCollection dbCollection;
	private String firstName, lastName, email, phone, countryCode, imei, nameToneDir;
	private String recording;
	private final String uri = "mongodb://prasadsawant5:seres2200@ds059692.mongolab.com:59692/namecoach";

	@SuppressWarnings("deprecation")
	@POST
	@Path("/register")
	@Consumes("application/json")
	@Produces(MediaType.TEXT_PLAIN)
	public String registerUser(String jsonStr) {

		mongoClientUri = new MongoClientURI(uri);
		mongoClient = new MongoClient(mongoClientUri);
		db = mongoClient.getDB(mongoClientUri.getDatabase());
		dbCollection = db.getCollection(COLLECTIONNAME);

		JSONObject obj = new JSONObject(jsonStr);
		
		firstName = obj.getString("first_name");
		lastName = obj.getString("last_name");
		email = obj.getString("email");
		phone = obj.getString("phone_number");
		countryCode = obj.getString("country_code");
		imei = obj.getString("device_id");
		nameToneDir = obj.getString("name_tone"); 
		
		
		System.out.println("FIRST NAME: " + firstName + "\n");
		System.out.println("LAST NAME: " + lastName + "\n");
		System.out.println("EMAIL: " + email + "\n");
		System.out.println("PHONE NUMBER: " + phone + "\n");
		System.out.println("COUNTRY CODE: " + countryCode + "\n");
		System.out.println("IMEI: " + imei + "\n");
		System.out.println("NAME TONE DIR: " + nameToneDir + "\n");

		
		BasicDBObject doc = new BasicDBObject("first_name", firstName).append("last_name", lastName)
				.append("email", email).append("phone_number", phone).append("country_code", countryCode)
				.append("device_imei", imei).append("name_tone_dir", nameToneDir);

		dbCollection.insert(doc);
		
		mongoClient.close();

		return "SUCCESS";
	}

	@POST
	@Path("/checkUser")
	@Consumes("application/json")
	@Produces(MediaType.APPLICATION_JSON)
	public String checkUser(String emailJson) {
		JSONObject res;
		StringBuilder sb = new StringBuilder();
		String response;
		
		try {
			mongoClientUri = new MongoClientURI(uri);
			mongoClient = new MongoClient(mongoClientUri);
			db = mongoClient.getDB(mongoClientUri.getDatabase());
			dbCollection = db.getCollection(COLLECTIONNAME);
		
			JSONObject obj = new JSONObject(emailJson);
			String email = obj.getString("email");
			
			System.out.println("JSON: " + obj.toString() + "\n");
			BasicDBObject basicDBObject = new BasicDBObject();
			basicDBObject.put("email", email);
			
			DBCursor dbCursor = dbCollection.find(basicDBObject);
			
			while (dbCursor.hasNext()) {
				sb.append(dbCursor.next().toString());
				//System.out.println(dbCursor.next());
			}
			
			response = sb.toString();
			
			mongoClient.close();
			
			if (response.length() > 0)
				return response;
			
			return null;
		} catch (Exception e) {
			mongoClient.close();
			System.out.println("EXCEPTION: " + e.getMessage() + " : " + e.getCause() + "\n");
			e.printStackTrace();
			return null;
		}
	}
	
	@POST
	@Path("/uploadTone")
	@Consumes("application/json")
	public void uploadTone(String file) {
		File someFile = new File("E:\\recording.3gp");
		JSONObject obj;
		String fileStream;
		byte[] fileBytes;
		BufferedOutputStream bos;
		try {
			obj = new JSONObject(file);
			fileStream = obj.getString("name_tone");
			fileBytes = Base64.decodeBase64(fileStream.getBytes());
			
			System.out.println(fileBytes);
			
			bos = new BufferedOutputStream(new FileOutputStream("E:\\recording.3gp"));
			bos.write(fileBytes);
			bos.flush();
			bos.close();
			
			
			
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}


