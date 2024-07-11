package com.swm_standard.phote.dto

import com.fasterxml.jackson.databind.JsonNode
import com.swm_standard.phote.entity.Question
import com.swm_standard.phote.entity.Tag
import java.time.LocalDateTime
import java.util.*

data class ReadQuestionDetailResponseDto(
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime? = null,
    val statement: String,
    val image: String? = null,
    val options: JsonNode? = null,
    val answer: String,
    val category: String,
    val tags: List<Tag>? = null,
    val memo: String? = null
) {

    constructor(question: Question, tags: List<Tag>?) : this(
        createdAt = question.createdAt,
        modifiedAt = question.modifiedAt,
        statement = question.statement,
        image = question.image,
        options = question.options,
        answer = question.answer,
        tags = tags,
        category = question.category,
        memo = question.memo
    )
}

class DeleteQuestionResponseDto (
    val id: UUID,
    val deletedAt: LocalDateTime
)