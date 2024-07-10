package com.swm_standard.phote.service

import com.swm_standard.phote.common.exception.AlreadyDeletedException
import com.swm_standard.phote.common.exception.NotFoundException
import com.swm_standard.phote.dto.CreateWorkbookResponse
import com.swm_standard.phote.dto.DeleteWorkbookResponse
import com.swm_standard.phote.dto.QuestionSetDto
import com.swm_standard.phote.dto.ReadWorkbookDetailResponse
import com.swm_standard.phote.entity.Workbook
import com.swm_standard.phote.repository.MemberRepository
import com.swm_standard.phote.repository.QuestionSetRepository
import com.swm_standard.phote.repository.WorkbookRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID

@Service
class WorkbookService(
    private val workbookRepository: WorkbookRepository,
    private val memberRepository: MemberRepository,
    private val questionSetRepository: QuestionSetRepository
) {

    fun createWorkbook(title: String, description: String?, emoji: String?, memberEmail: String): CreateWorkbookResponse {
        val member = memberRepository.findByEmail(memberEmail) ?: throw NotFoundException()
        val workbook = workbookRepository.save(Workbook(title, description, member, emoji))

        return CreateWorkbookResponse(workbook.id)
    }

    @Transactional
    fun deleteWorkbook(id: UUID): DeleteWorkbookResponse {
        val workbook = workbookRepository.findById(id).orElseThrow { NotFoundException("존재하지 않는 workbook") }
        if (workbook.isDeleted()) throw AlreadyDeletedException("workbook")

        workbook.deletedAt = LocalDateTime.now()
        val deletedWorkbook = workbookRepository.save(workbook)

        return DeleteWorkbookResponse(deletedWorkbook.id, deletedWorkbook.deletedAt!!)
    }

    fun readWorkbookDetail(id: UUID) : ReadWorkbookDetailResponse {
        val workbook = workbookRepository.findById(id).orElseThrow { NotFoundException(message = "존재하지 않는 workbook") }
        if (workbook.isDeleted()) throw AlreadyDeletedException("workbook")

        val questionSet = questionSetRepository.findAllByWorkbookId(id)

        val questionSetDto: List<QuestionSetDto> = questionSet.filter { !it.isDeleted() }.map { set ->
            QuestionSetDto(
                set.sequence,
                set.question
            )
        }

        return ReadWorkbookDetailResponse(
            workbook.id,
            workbook.title,
            workbook.description,
            workbook.emoji!!,
            workbook.createdAt,
            workbook.modifiedAt,
            questionSetDto
            )
    }
}