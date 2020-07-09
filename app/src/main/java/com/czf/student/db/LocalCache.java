package com.czf.student.db;

import org.greenrobot.greendao.annotation.*;

@Entity
public class LocalCache {
    @Id(autoincrement = true)
    private long id;

    @Property(nameInDb = "label")
    @Index(unique = true)
    private String label;

    @Property(nameInDb = "content")
    private String content;

    @Generated(hash = 281212707)
    public LocalCache() {
    }

    @Generated(hash = 72238312)
    public LocalCache(long id, String label, String content) {
        this.id = id;
        this.label = label;
        this.content = content;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}