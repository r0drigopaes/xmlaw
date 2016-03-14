/*
GNU Mages, version 1.00 alpha
Multi-Agents Environment Simulator
Copyright (C) 2001-2002 João Ricardo Bittencourt <jrbitt@uol.com.br>
 
This program is free software; you can redistribute it and/or modify 
it under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version. 

This program is distributed in the hope that it will be useful, 
but WITHOUT ANY WARRANTY; without even the implied warranty of 
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
GNU General Public License for more details. 

You should have received a copy of the GNU General Public License 
along with this program; if not, write to the Free Software 
Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
*/


package mages.util;

import java.io.*;

public class User implements Serializable, Cloneable{
  String name;
  String institution;
  String email;
  String website;
  String country;

  public User(){
    name = "Default";
    institution = "Default";
    email = "Default";
    website = "Default";
    country = "Default";  
  }
  
  public void setName(String n){
    name = n;
  }

  public String getName(){
    return name;
  }

  public void setInstitution(String i){
    institution = i;
  }

  public String getInstitution(){
    return institution;
  }

  public void setEmail(String e){
    email = e;
  }

  public String getEmail(){
    return email;
  }

  public void setWebsite(String w){
    website = w;
  }

  public String getWebsite(){
    return website;
  }

  public void setCountry(String c){
    country = c;
  }

  public String getCountry(){
    return country;
  }
  
  public void print(){
    System.out.println("Name        : "+name);
    System.out.println("E-mail      : "+email);    
    System.out.println("Site        : "+website);    
    System.out.println("Institution : "+institution);    
    System.out.println("Country     : "+country);    
  }
  
   public Object clone(){
     User u = new User();
     u.setName(new String(getName()));
     u.setInstitution(new String(getInstitution()));     
     u.setEmail(new String(getEmail()));     
     u.setWebsite(new String(getWebsite()));     
     u.setCountry(new String(getCountry()));     
     return u;
   }  
}
