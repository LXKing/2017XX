package com.xiangxun.bean;

import java.io.Serializable;

public class RoadInfo implements Serializable{
	private static final long serialVersionUID = 7893208001838029470L;

    public String id;
    public String name;
    public String note;
    public String groupId;
    public String pid;
    public Integer levels;
    public String status;
    public String uploadcode;
    public String coderoadtype;
    public String orgId;
    public String orgName;    
    //扩展FORM属性
    public String codeRoadType;
    public String codeRoadDh;
    public String codeRoadZh;
    public String codeRoadMi;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note == null ? null : note.trim();
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId == null ? null : groupId.trim();
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid == null ? null : pid.trim();
    }

   

    public Integer getLevels() {
		return levels;
	}

	public void setLevels(Integer levels) {
		this.levels = levels;
	}

	public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public String getUploadcode() {
        return uploadcode;
    }

    public void setUploadcode(String uploadcode) {
        this.uploadcode = uploadcode == null ? null : uploadcode.trim();
    }

    public String getCoderoadtype() {
        return coderoadtype;
    }

    public void setCoderoadtype(String coderoadtype) {
        this.coderoadtype = coderoadtype == null ? null : coderoadtype.trim();
    }

	public String getCodeRoadType() {
		return codeRoadType;
	}

	public void setCodeRoadType(String codeRoadType) {
		this.codeRoadType = codeRoadType;
	}

	public String getCodeRoadDh() {
		return codeRoadDh;
	}

	public void setCodeRoadDh(String codeRoadDh) {
		this.codeRoadDh = codeRoadDh;
	}

	public String getCodeRoadZh() {
		return codeRoadZh;
	}

	public void setCodeRoadZh(String codeRoadZh) {
		this.codeRoadZh = codeRoadZh;
	}

	public String getCodeRoadMi() {
		return codeRoadMi;
	}

	public void setCodeRoadMi(String codeRoadMi) {
		this.codeRoadMi = codeRoadMi;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
    
}