package models;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import play.db.jpa.JPASupport;

@MappedSuperclass
public class Model extends JPASupport {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public Long id;

    public Long getId() {
        return id;
    }
}
