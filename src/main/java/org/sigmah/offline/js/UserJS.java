package org.sigmah.offline.js;

import java.util.List;

import org.sigmah.shared.dto.UserDTO;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;
import org.sigmah.shared.dto.profile.ProfileDTO;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import java.util.ArrayList;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class UserJS extends JavaScriptObject {
	
	protected UserJS() {
	}
	
	public static UserJS toJavaScript(UserDTO userDTO) {
		final UserJS userJS = Values.createJavaScriptObject(UserJS.class);
		
		userJS.setId(userDTO.getId());
		userJS.setOrganization((Integer)userDTO.get("organization"));
		userJS.setName(userDTO.getName());
		userJS.setEmail(userDTO.getEmail());
		userJS.setFirstName(userDTO.getFirstName());
		userJS.setCompleteName(userDTO.getCompleteName());
		userJS.setLocale(userDTO.getLocale());
		userJS.setOrgUnit(userDTO.getOrgUnit());
		userJS.setProfiles(userDTO.getProfiles());
		
		return userJS;
	}
	
	public UserDTO toDTO() {
		final UserDTO userDTO = new UserDTO();
		
		userDTO.setId(getId());
		userDTO.setName(getName());
		userDTO.setEmail(getEmail());
		userDTO.setFirstName(getFirstName());
		userDTO.setCompleteName(getCompleteName());
		userDTO.setLocale(getLocale());
		userDTO.setProfiles(getProfilesDTO());
		
		return userDTO;
	}
	
	public native int getId() /*-{
		return this.id;
	}-*/;
	
	public native void setId(int id) /*-{
		this.id = id;
	}-*/;
	
	public native void setOrganization() /*-{
		this.organization = undefined;
	}-*/;
	
	public void setOrganization(Integer id) {
		if(id != null) {
			setOrganization(id.intValue());
		} else {
			setOrganization();
		}
	}
	
	public native void setOrganization(int id) /*-{
		this.organization = id;
	}-*/;
	
	public native int getOrganization() /*-{
		return this.organization;
	}-*/;
	
	public native String getName() /*-{
		return this.name;
	}-*/;
			
	public native void setName(String name) /*-{
		this.name = name;
	}-*/;

	public native String getEmail() /*-{
		return this.email;
	}-*/;

	public native void setEmail(String email) /*-{
		this.email = email;
	}-*/;

	public native String getFirstName() /*-{
		return this.firstName;
	}-*/;

	public native void setFirstName(String firstName) /*-{
		this.firstName = firstName;
	}-*/;

	public native String getCompleteName() /*-{
		return this.completeName;
	}-*/;

	public native void setCompleteName(String completeName) /*-{
		this.completeName = completeName;
	}-*/;

	public native String getLocale() /*-{
		return this.locale;
	}-*/;

	public native void setLocale(String locale) /*-{
		this.locale = locale;
	}-*/;

	public native boolean hasOrgUnit() /*-{
		return typeof this.orgUnit != 'undefined';
	}-*/;
	
	public native int getOrgUnit() /*-{
		return this.orgUnit;
	}-*/;

	public void setOrgUnit(OrgUnitDTO orgUnitDTO) {
		if(orgUnitDTO != null) {
			setOrgUnit(orgUnitDTO.getId());
		}
	}
	
	public native void setOrgUnit(int orgUnit) /*-{
		this.orgUnit = orgUnit;
	}-*/;

	public native JsArray<ProfileJS> getProfiles() /*-{
		return this.profiles;
	}-*/;
	
	public List<ProfileDTO> getProfilesDTO() {
		if(getProfiles() != null) {
			final List<ProfileDTO> profilesDTO = new ArrayList<ProfileDTO>();
			
			final JsArray<ProfileJS> profilesJS = getProfiles();
			for(int index = 0; index < profilesJS.length(); index++) {
				profilesDTO.add(profilesJS.get(index).toDTO());
			}
			
			return profilesDTO;
		}
		return null;
	}

	public void setProfiles(List<ProfileDTO> profilesDTO) {
		if(profilesDTO != null) {
			final JsArray<ProfileJS> profilesJS = Values.createTypedJavaScriptArray(ProfileJS.class);
			
			for(final ProfileDTO profileDTO : profilesDTO) {
				profilesJS.push(ProfileJS.toJavaScript(profileDTO));
			}
			setProfiles(profilesJS);
		}
	}
	
	public native void setProfiles(JsArray<ProfileJS> profiles) /*-{
		this.profiles = profiles;
	}-*/;

	public native boolean isActive() /*-{
		return this.active;
	}-*/;

	public native void setActive(boolean active) /*-{
		this.active = active;
	}-*/;
}