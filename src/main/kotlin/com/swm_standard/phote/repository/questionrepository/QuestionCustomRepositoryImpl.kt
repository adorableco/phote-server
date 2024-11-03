package com.swm_standard.phote.repository.questionrepository

import com.querydsl.jpa.impl.JPAQueryFactory
import com.swm_standard.phote.dto.SearchQuestionsToAddResponse
import com.swm_standard.phote.entity.QQuestion
import com.swm_standard.phote.entity.QQuestionSet
import com.swm_standard.phote.entity.QTag
import com.swm_standard.phote.entity.Question
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class QuestionCustomRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
) : QuestionCustomRepository {
    override fun searchQuestionsList(
        memberId: UUID,
        tags: List<String>?,
        keywords: List<String>?,
    ): List<Question> {
        val question = QQuestion.question
        val tag = QTag.tag

        val query =
            jpaQueryFactory
                .selectFrom(question)
                .where(question.member.id.eq(memberId))
                .leftJoin(question.tags, tag)
                .fetchJoin()

        // 태그 조건: tags로 들어온 태그들을 모두 포함하는 문제 검색
        if (!tags.isNullOrEmpty()) {
            val tagCondition =
                tags.map { tagName ->
                    jpaQueryFactory
                        .selectFrom(tag)
                        .where(tag.name.eq(tagName).and(tag.question.eq(question)))
                        .exists()
                }
            // reduce: 서브쿼리(sub)들을 and조건으로 결합
            query.where(tagCondition.reduce { sub, predicate -> sub.and(predicate) })
        }

        // 문항 조건: keywords로 들어온 검색어를 모두 포함하는 문항(statement)을 가진 문제 검색
        if (!keywords.isNullOrEmpty()) {
            val keywordConditions =
                keywords.map { keyword ->
                    question.statement.contains(keyword)
                }
            query.where(keywordConditions.reduce { acc, predicate -> acc.and(predicate) })
        }

        return query
            .orderBy(question.modifiedAt.desc())
            .fetch()
    }

    override fun searchQuestionsToAddList(
        memberId: UUID,
        workbookId: UUID,
        tags: List<String>?,
        keywords: List<String>?,
    ): List<SearchQuestionsToAddResponse> {
        val questionSet = QQuestionSet.questionSet

        val questionsInWorkbook =
            jpaQueryFactory
                .select(questionSet.question.id)
                .from(questionSet)
                .where(questionSet.workbook.id.eq(workbookId))
                .fetch()

        val questions = searchQuestionsList(memberId, tags, keywords)

        return questions.map { question ->
            val options = question.options?.let { question.deserializeOptions() }
            SearchQuestionsToAddResponse(
                questionId = question.id,
                createdAt = question.createdAt,
                modifiedAt = question.modifiedAt,
                statement = question.statement,
                image = question.image,
                options = options,
                answer = question.answer,
                category = question.category,
                tags = question.tags,
                memo = question.memo,
                isContain = question.id in questionsInWorkbook,
            )
        }
    }
}
