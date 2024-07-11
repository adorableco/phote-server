package com.swm_standard.phote.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.swm_standard.phote.common.exception.AlreadyDeletedException
import com.swm_standard.phote.common.exception.BadRequestException
import com.swm_standard.phote.common.exception.NotFoundException
import com.swm_standard.phote.dto.DeleteQuestionResponseDto
import com.swm_standard.phote.dto.ReadQuestionDetailResponseDto
import com.swm_standard.phote.repository.QuestionRepository
import com.swm_standard.phote.repository.QuestionSetRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID

@Service
class QuestionService (
    private val questionRepository: QuestionRepository,
    private val questionSetRepository: QuestionSetRepository) {
    @Transactional
    fun readQuestionDetail(id: UUID): ReadQuestionDetailResponseDto {
        val question = questionRepository.findById(id).orElseThrow { NotFoundException("questionId","존재하지 않는 UUID") }

        // options가 없는 주관식일 경우
        return ReadQuestionDetailResponseDto(question)
    }

    @Transactional
    fun deleteQuestion(id: UUID): DeleteQuestionResponseDto {

        // 존재하지 않는 question id가 아닌지 확인
        questionRepository.findById(id).orElseThrow { NotFoundException("questionId","존재하지 않는 UUID") }

        questionRepository.deleteById(id)

        return DeleteQuestionResponseDto(id, LocalDateTime.now())
    }
}