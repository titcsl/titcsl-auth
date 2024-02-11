package space.titcsl.auth.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Data
@EntityListeners(AuditingEntityListener.class)

@Table(name = "_xlock_user")
public class LockAccountRequest {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "lock_id", unique = true)
    private String lock_id;
    private String locked_by;
    private String email;
    private String request_id;
    private String why;
    private String unlock_by;
    @CreatedDate
    private LocalDateTime created_at;

    @LastModifiedDate
    private LocalDateTime lastUpdateOn;
}
