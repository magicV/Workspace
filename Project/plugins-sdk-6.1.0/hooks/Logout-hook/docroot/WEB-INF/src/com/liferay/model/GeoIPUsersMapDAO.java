/**
 * Copyright (c) 2000-2010 Liferay, Inc. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.liferay.model;

import com.liferay.util.LPortalConnectionPool;
import javax.portlet.RenderRequest;
import com.liferay.model.UserVO;
import com.liferay.model.MarkVO;

import com.liferay.util.portlet.PortletProps;

import javax.portlet.PortletSession;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Date;
import java.util.Random;

import com.maxmind.geoip.Location;
import com.maxmind.geoip.LookupService;

import com.maxmind.geoip.CountryCodes;
import com.maxmind.geoip.LatLngCountryBean;
import com.maxmind.geoip.LatLngfromCountryCodeBean;

/**
 * <a href="GeoIPUsersMapDAO.java.html"><b><i>View Source</i></b></a>
 *
 * @author Jose Miguel Trinchan
 *
 */

public class GeoIPUsersMapDAO {

	//constants
	private static final String _LOCALNET_ADDRESS = "192.168.0.X"; //CHANGE YOUR LOCALNET ADDRESS HERE TO IGNORE THATS ENTRIES
	
	private static final String _GET_USER_DATA = "select userId, screenName, loginIP, firstName, lastName from User_ where screenName<>'webmaster';";
	
	private static final String _GET_USER_DATA_BY_ID = "select userId, screenName, lastLoginIP, firstName, lastName, location from User_ where userid=?;";
	
	private static final String _UPDATE_USER_LOCATION = "update User_ set location= ? where userid=?;";
	
	private static final String _UPDATE_IP = "update User_ set lastloginIP= ? where userid=?;";
	
	private static final String _GET_USER_LOCATION = "select location from  User_  where userid=?;";
	
	private static final String _UPDATE_IS_AUTO_LOCATION = "update User_ set is_auto= ? where userid=?;";
	
	private static final String _UPDATE_USER_STATUS = "update User_ set isonline= ? where userid=?;";
	
	private static final String _UPDATE_LYNC = "update CONTACT_ set aimsn= ? where userid=?;";
	private static final String _UPDATE_GTALK = "update CONTACT_ set icqsn= ? where userid=?;";
	private static final String _UPDATE_MSN = "update CONTACT_ set msnsn= ? where userid=?;";
	private static final String _UPDATE_SKYPE = "update CONTACT_ set skypesn= ? where userid=?;";
	
	private static final String _GET_USER_SCREEN_NAME = "select username from  User_  where userid=?;";
	
	public static void importIMfromLDAP( String userId, LDAPUserInfo ldapUser)
	{
		System.out.println(" START   importIMfromLDAP ######## UserID : "+userId + " ldapUser : "+ldapUser );
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try 
		{		
			con = LPortalConnectionPool.getConnection();
			
			ps = con.prepareStatement(_UPDATE_LYNC);
			ps.setString(1, ldapUser.sip );
			ps.setInt(2, Integer.parseInt(userId) );
			int res =ps.executeUpdate();			
			System.out.println("���������  res : "+res);
			
			ps = con.prepareStatement(_UPDATE_GTALK);
			ps.setString(1, ldapUser.gtalk );
			ps.setInt(2, Integer.parseInt(userId) );
			 res =ps.executeUpdate();			
			System.out.println("���������  res : "+res);
			
			
			ps = con.prepareStatement(_UPDATE_MSN);
			ps.setString(1, ldapUser.msn );
			ps.setInt(2, Integer.parseInt(userId) );
			 res =ps.executeUpdate();			
			System.out.println("���������  res : "+res);
			
			
			ps = con.prepareStatement(_UPDATE_SKYPE);
			ps.setString(1, ldapUser.skype );
			ps.setInt(2, Integer.parseInt(userId) );
			 res =ps.executeUpdate();			
			System.out.println("���������  res : "+res);
			
		
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		finally {LPortalConnectionPool.cleanUp(con, ps, rs);}
		
		System.out.println(" END   importIMfromLDAP ######## UserID : "+userId + " ldapUser : "+ldapUser );
		
		
	}
	public static boolean updateUserStatus( String userId, boolean isOnline )
	{
		System.out.println(" ################################################");
		System.out.println(" START   updateUserStatus ######## UserID : "+userId + " isOnline : "+isOnline );
		System.out.println(" ################################################");
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try 
		{
			
			con = LPortalConnectionPool.getConnection();
			ps = con.prepareStatement(_UPDATE_USER_STATUS);
			ps.setBoolean(1, isOnline );
			ps.setInt(2, Integer.parseInt(userId) );
			int res =ps.executeUpdate();
			
			System.out.println("���������  res : "+res);
			
			if( res >= 0 )
			{
				return true;
			}
			
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		finally {LPortalConnectionPool.cleanUp(con, ps, rs);}
		
		return false;
		
	}
	
	public String getUserCode( String userId )
	{
		System.out.println( " &&&&&&&&&&&&&&&&&  getUserCode  userId : "+userId );
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try 
		{
			
			con = LPortalConnectionPool.getConnection();
			ps = con.prepareStatement(_GET_USER_LOCATION);
			ps.setInt(1, Integer.parseInt(userId) );
			rs=ps.executeQuery();
			while (rs.next()) 
			{				
				String code = rs.getString(1);
				System.out.println( " 222222222222222222  getUserCode  userId : "+userId );
				if( code!=null) code =code.trim();
				
				System.out.println( "����   code from DB : "+ code +" with userId : "+userId );
				
//				if( code!="" || code.isEmpty() )
//				{
//					
//						String countryName = com.liferay.portlet.GeoIPUsersMapPortlet.getUserCountry();
//					
//						CountryCodes cc = new CountryCodes();
//					
//						if(countryName!= null )
//						{
//							code =  cc.getCountryCodeByName( countryName );
//							
//							System.out.println( "����   code from URL  : "+ code + " for countryName : "+countryName);
//						}
//					
//					
//				}
				
				return code; 
				
			}
			
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		finally {LPortalConnectionPool.cleanUp(con, ps, rs);}
		
		return null;
		
	}
	
	public boolean updateUserLocation( String userId, String code, String isAutoSelected )
	{
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			
			con = LPortalConnectionPool.getConnection();
			ps = con.prepareStatement(_UPDATE_USER_LOCATION);
			ps.setString(1, code);
			ps.setInt(2, Integer.parseInt(userId) );
			int res =ps.executeUpdate();
			
			System.out.println("��������� _UPDATE_USER_LOCATION  res : "+res);
			
			if( isAutoSelected!=null && isAutoSelected!="" )
			{
				ps = con.prepareStatement(_UPDATE_IS_AUTO_LOCATION);
				ps.setInt(1, Integer.parseInt(isAutoSelected) );
				ps.setInt(2, Integer.parseInt(userId) );
				 res =ps.executeUpdate();
				
				System.out.println("���������_UPDATE_IS_AUTO_LOCATION  res : "+res);
				
			}
			
			
			if( res >= 0 )
			{
				return true;
			}
			
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		finally {LPortalConnectionPool.cleanUp(con, ps, rs);}
		
		return false;
		
	}
	public static boolean updateIP( String userId, String ip )
	{
		System.out.println(" ################################################");
		System.out.println(" updateIP ######## UserID : "+userId + " ip : "+ip );
		System.out.println(" ################################################");
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try 
		{
			
			con = LPortalConnectionPool.getConnection();
			ps = con.prepareStatement(_UPDATE_IP);
			ps.setString(1, ip.trim() );
			ps.setInt(2, Integer.parseInt(userId) );
			int res =ps.executeUpdate();
			
			System.out.println("���������  res : "+res);
			
			if( res >= 0 )
			{
				return true;
			}
			
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		finally {LPortalConnectionPool.cleanUp(con, ps, rs);}
		
		return false;
		
	}

	public static List<MarkVO> getUsersData() {
		List<MarkVO>  ret = new ArrayList<MarkVO>();
		List<UserVO> userList = new ArrayList<UserVO>();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		UserVO user = new UserVO();
		Location location = null;
		LookupService lookupService = null;	
		try {
			lookupService = new LookupService(PortletProps.get("maxmind.database.file"),LookupService.GEOIP_MEMORY_CACHE);
			if (lookupService != null) {			
				con = LPortalConnectionPool.getConnection();
				ps = con.prepareStatement(_GET_USER_DATA);
				rs=ps.executeQuery();
				while (rs.next()) {
					String ip = rs.getString(3);
					if( ip!=null && ip.trim().equals("127.0.0.1"))
					{
						ip="94.200.128.203";
					}
					if (ip.length()>6) {
						System.out.println(" ***************************** Location : "+rs.getString(5)+" Latitude :"+rs.getString(6)+" Longitude : "+rs.getString(7) );
						String loc = rs.getString(5);
						Float lat =  rs.getString(6)!=null ? Float.parseFloat( rs.getString(6) ) : null;
						Float lng =  rs.getString(7)!=null ? Float.parseFloat( rs.getString(7) ) : null;
						
						if( loc!=null && loc!=""  )
						{
							CountryCodes cc = new CountryCodes();
							
							LatLngfromCountryCodeBean llcc = new LatLngfromCountryCodeBean();
							LatLngCountryBean ltbean = llcc.getBean( loc );
							
							String cName = cc.getCountry( loc ); 
							location = new Location();
							location.countryCode = loc;
							location.countryName = cName;
							
							location.latitude = Float.parseFloat( new Double( ltbean.Lat ).toString() );
							location.longitude =Float.parseFloat( new Double(ltbean.Long).toString());
							
							System.out.println(" IF  ***************************** Location : "+rs.getString(5)+" Latitude :"+rs.getString(6)+" Longitude : "+rs.getString(7) );
							
						}
						else
						{
							location = lookupService.getLocation(ip);
						}
						
						user= new UserVO(rs.getInt(1),rs.getString(2),ip, location,rs.getString(4),rs.getString(5));
						userList.add(user);
					}
				}
			}
		for (int i=0;i<userList.size();i++) {
			user = (UserVO) userList.get(i);
			boolean encontrado = false;
			int j = 0;
			MarkVO mark = new MarkVO();
			while ((!encontrado)&&(j<ret.size())&&(!GeoIPUsersMapDAO.isLocalIP(user.getIp()))) {
				mark = (MarkVO) ret.get(j);
				j++;
				if (user.isSameLocation(mark)) {
					encontrado=true;
					mark.addScreenname(user.getScreenname());
				}
			}
			if ((!encontrado)&&(!GeoIPUsersMapDAO.isLocalIP(user.getIp()))) {
					location=user.getLocation();
					List list = new ArrayList();
					list.add(user.getScreenname());
					ret.add(new MarkVO(list,location, user.getFirstName(), user.getLastName()));
			}

			
			
		}
		} catch (Exception e) {e.printStackTrace();}
    
		finally {LPortalConnectionPool.cleanUp(con, ps, rs);}
		
		return ret;
}

	public static Integer getNumUsers() {
		Integer ret = 0; 
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String ip = null;
		try {
			con = LPortalConnectionPool.getConnection();
			ps  = con.prepareStatement(_GET_USER_DATA);
			rs = ps.executeQuery();
			while (rs.next()) { 
				ip = rs.getString(3);
				if( ip!=null && ip.trim().equals("127.0.0.1"))
				{
					ip="94.200.128.203";
				}
				if ((ip!=null)&&(ip!="")&&(ip.length()>7)) {if(!GeoIPUsersMapDAO.isLocalIP(ip)) ret++;}
			}	
		} catch (Exception e) {e.printStackTrace();}
    		
		finally {LPortalConnectionPool.cleanUp(con, ps, rs);}

		return ret;
	}

	public static boolean isLocalIP(String ip) {
		boolean ret = true;
		try{		
			String aux = _LOCALNET_ADDRESS.substring(0,_LOCALNET_ADDRESS.indexOf("X")-1);
			String[] splitedAux = aux.split("\\.");
			
			String[] splitedIP  = ip.split("\\.");

			for (int i=0;i<splitedAux.length;i++) {if (!splitedAux[i].equals(splitedIP[i])) ret=false;}
		} catch (Exception e) {e.printStackTrace();}
		return ret;
	}
	public static List<MarkVO> getUsersData( long userId, RenderRequest renderRequest) {
		System.out.println("******** getUsersData is called with userId :"+userId );
		
		
		
		List<MarkVO>  ret = new ArrayList<MarkVO>();
		List<UserVO> userList = new ArrayList<UserVO>();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		UserVO user = new UserVO();
		Location location = null;
		LookupService lookupService = null;	
		try {
			//updateIP( new Long(userId).toString(), com.liferay.portlet.GeoIPUsersMapPortlet.getUserIP() );
			lookupService = new LookupService(PortletProps.get("maxmind.database.file"),LookupService.GEOIP_MEMORY_CACHE);
			System.out.println("1"+lookupService);
			if (lookupService != null) {			
				con = LPortalConnectionPool.getConnection();
				System.out.println("2"+con);
				ps = con.prepareStatement(_GET_USER_DATA_BY_ID);
				ps.setLong(1, userId);
				rs=ps.executeQuery();
				System.out.println("3 rs : "+rs);
				while (rs.next()) {
					String ip = rs.getString(3);
					if( ip!=null && ip.trim().equals("127.0.0.1"))
					{
						ip="94.200.128.203";
					}
					if (ip.length()>6) {
						System.out.println(" 4.5 ip : "+ip );
						
						Object obj = renderRequest.getPortletSession().getAttribute("code-"+renderRequest.getRemoteUser(),  PortletSession.APPLICATION_SCOPE);
					   System.out.println( " obj : "+obj );
						if( obj == null )
					    {
					    	location = lookupService.getLocation(ip);
					    }
					    else
					    {
					    	location = new Location();
					    	location.countryCode= rs.getString(6);
					    	location.countryName = CountryCodes.getCountry( rs.getString(6) );
					    }
						
						user= new UserVO(rs.getInt(1),rs.getString(2),ip, location,rs.getString(4),rs.getString(5));
						userList.add(user);
					}
				}
			}
			System.out.println(" 4 userList.size() : "+ userList.size() );
		for (int i=0;i<userList.size();i++) {
			user = (UserVO) userList.get(i);
			boolean encontrado = false;
			int j = 0;
			MarkVO mark = new MarkVO();
			while ((!encontrado)&&(j<ret.size())&&(!GeoIPUsersMapDAO.isLocalIP(user.getIp()))) {
				mark = (MarkVO) ret.get(j);
				j++;
				if (user.isSameLocation(mark)) {
					encontrado=true;
					mark.addScreenname(user.getScreenname());
				}
			}
			if ((!encontrado)&&(!GeoIPUsersMapDAO.isLocalIP(user.getIp()))) {
					location=user.getLocation();
					System.out.println("5 location "+ location.countryCode );
			 renderRequest.getPortletSession().setAttribute("code-"+renderRequest.getRemoteUser(), location.countryCode,  PortletSession.APPLICATION_SCOPE);
					
					List list = new ArrayList();
					list.add(user.getScreenname());
					ret.add(new MarkVO(list,location, user.getFirstName(), user.getLastName()));
			}

			
			
		}
		} catch (Exception e) {e.printStackTrace();}
    
		finally {LPortalConnectionPool.cleanUp(con, ps, rs);}
		
		return ret;
}
	public static String getScreenName( String userId )
	{
		System.out.println( " &&&&&&&&&&&&&&&&&  getScreenName  userId : "+userId );
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;	
		try 
		{			
			con = LPortalConnectionPool.getConnection();
			ps = con.prepareStatement(_GET_USER_SCREEN_NAME);
			ps.setInt(1, Integer.parseInt(userId) );
			rs=ps.executeQuery();
			while (rs.next()) 
			{				
				String username = rs.getString(1);
				System.out.println( " 222222222222222222  getUserCode  userId : "+userId );
				if( username!=null) username =username.trim();				
				System.out.println( "����   username from DB : "+ username +" with userId : "+userId );
				return username; 				
			}			
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		finally {LPortalConnectionPool.cleanUp(con, ps, rs);}
		
		return null;
		
	}

}
