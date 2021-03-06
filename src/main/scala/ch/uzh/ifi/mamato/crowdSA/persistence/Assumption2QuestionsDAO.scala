package ch.uzh.ifi.mamato.crowdSA.persistence

import ch.uzh.ifi.mamato.crowdSA.model.AssumptionQuestion
import scalikejdbc._

/**
 * Created by mattia on 19.03.15.
 */
object Assumption2QuestionsDAO extends SQLSyntaxSupport[AssumptionQuestion]{

  override val tableName = "assumption2questions"

  def apply(p: SyntaxProvider[AssumptionQuestion])(rs: WrappedResultSet): AssumptionQuestion = apply(p.resultName)(rs)
  def apply(p: ResultName[AssumptionQuestion])(rs: WrappedResultSet): AssumptionQuestion = new AssumptionQuestion(rs.long(p.id), rs.long(p.assumption_id), rs.string(p.question), rs.string(p.test_names))

  val p = Assumption2QuestionsDAO.syntax("p")

  def find(id: Long)(implicit session: DBSession = autoSession): Option[AssumptionQuestion] = withSQL {
    select.from(Assumption2QuestionsDAO as p).where.eq(p.id, id)//.and.append(isNotDeleted)
  }.map(Assumption2QuestionsDAO(p)).single.apply()

  def findByAssumptionQuestion(question: String)(implicit session: DBSession = autoSession): Option[AssumptionQuestion] = withSQL {
    select.from(Assumption2QuestionsDAO as p)
      .where.eq(p.question, question)
      .orderBy(p.id)
  }.map(Assumption2QuestionsDAO(p)).single.apply()

  def findByAssumptionId(id: Long)(implicit session: DBSession = autoSession): List[AssumptionQuestion] = withSQL {
    select.from(Assumption2QuestionsDAO as p)
      .where.eq(p.assumption_id, id)
      .orderBy(p.id)
  }.map(Assumption2QuestionsDAO(p)).list.apply()

  def findAll()(implicit session: DBSession = autoSession): List[AssumptionQuestion] = withSQL {
    select.from(Assumption2QuestionsDAO as p)
      .orderBy(p.id)
  }.map(Assumption2QuestionsDAO(p)).list.apply()

  def countAll()(implicit session: DBSession = autoSession): Long = withSQL {
    select(sqls.count).from(Assumption2QuestionsDAO as p)//.where.append(isNotDeleted)
  }.map(rs => rs.long(1)).single.apply().get

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[AssumptionQuestion] = withSQL {
    select.from(Assumption2QuestionsDAO as p).where.append(sqls"${where}")
      .orderBy(p.id)
  }.map(Assumption2QuestionsDAO(p)).list.apply()

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = withSQL {
    select(sqls.count).from(Assumption2QuestionsDAO as p).where.append(sqls"${where}")//.and.append(isNotDeleted)
  }.map(_.long(1)).single.apply().get

  def create(assumption_id: Long, question: String, test_names: String)(implicit session: DBSession = autoSession): AssumptionQuestion = {
    val id = withSQL {
      insert.into(Assumption2QuestionsDAO).namedValues(
        column.assumption_id -> assumption_id,
        column.question -> question,
        column.test_names -> test_names)
    }.updateAndReturnGeneratedKey.apply()
    find(id).get
  }

  def save(m: AssumptionQuestion)(implicit session: DBSession = autoSession): AssumptionQuestion = {
    withSQL {
      update(Assumption2QuestionsDAO).set(
        column.assumption_id -> m.assumption_id,
        column.question -> m.question,
        column.test_names -> m.test_names
      ).where.eq(column.id, m.id)//.and.isNull(column.deletedAt)
    }.update.apply()
    m
  }

}
