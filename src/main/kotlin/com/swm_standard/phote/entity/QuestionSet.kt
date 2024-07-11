package com.swm_standard.phote.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import java.time.LocalDateTime

@Entity
@SQLDelete(sql = "UPDATE question_set SET deleted_at = NOW() WHERE question_set_id = ?")
@SQLRestriction("deleted_at is NULL")
data class QuestionSet(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "questionSet_id")
    val id: Long,

    @ManyToOne
    @JoinColumn(name = "question_id")
    val question: Question,

    @ManyToOne
    @JoinColumn(name = "workbook_id")
    @JsonIgnore
    val workbook: Workbook,

    val sequence: Int,

    @CreationTimestamp
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @JsonIgnore
    var deletedAt: LocalDateTime?,
    )
