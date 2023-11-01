package ru.practicum.ewm.comment.model;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments_versions")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentVersion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    @Column
    private LocalDateTime changed;

    @ManyToOne
    @JoinColumn(name = "reply_to_comment_id")
    private Comment replyToComment;

    @Length(min = 2, max = 1000)
    @Column(nullable = false)
    private String text;

}
