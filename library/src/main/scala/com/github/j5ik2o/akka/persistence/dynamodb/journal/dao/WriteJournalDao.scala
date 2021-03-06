package com.github.j5ik2o.akka.persistence.dynamodb.journal.dao

import akka.NotUsed
import akka.actor.Scheduler
import akka.persistence.PersistentRepr
import akka.stream.scaladsl.Source
import com.github.j5ik2o.akka.persistence.dynamodb.journal.{ JournalRow, PersistenceId, SequenceNumber }

import scala.concurrent.duration.FiniteDuration
import scala.util.Try

trait WriteJournalDao extends JournalDaoWithReadMessages {
  import com.github.j5ik2o.akka.persistence.dynamodb.journal.{ PersistenceId, SequenceNumber }

  def deleteMessages(
      persistenceId: PersistenceId,
      toSequenceNr: SequenceNumber
  ): Source[Long, NotUsed]

  def highestSequenceNr(persistenceId: PersistenceId, fromSequenceNr: SequenceNumber): Source[Long, NotUsed]

  def putMessages(messages: Seq[JournalRow]): Source[Long, NotUsed]

}

trait JournalDaoWithUpdates extends WriteJournalDao {

  def updateMessage(journalRow: JournalRow): Source[Unit, NotUsed]

}

trait JournalDaoWithReadMessages {

  def getMessages(
      persistenceId: PersistenceId,
      fromSequenceNr: SequenceNumber,
      toSequenceNr: SequenceNumber,
      max: Long,
      deleted: Option[Boolean] = Some(false)
  ): Source[Try[PersistentRepr], NotUsed]

  def getMessagesWithBatch(
      persistenceId: String,
      fromSequenceNr: Long,
      toSequenceNr: Long,
      batchSize: Int,
      refreshInterval: Option[(FiniteDuration, Scheduler)]
  ): Source[Try[PersistentRepr], NotUsed]

}
