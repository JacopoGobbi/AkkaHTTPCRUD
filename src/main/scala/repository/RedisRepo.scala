package repository

import redis.{ByteStringSerializer, RedisClient}

import scala.concurrent.Future
import scala.concurrent.duration.Duration

trait Repo {
  def del(key: String): Future[Long]

  def upsert[V](
    key: String,
    value: V,
    expire: Option[Duration] = None
  )(implicit ev: ByteStringSerializer[V]): Future[Boolean]

  def get(key: String): Future[Option[String]]
}

trait RedisRepo extends Repo {

  def db: RedisClient

  override def del(key: String): Future[Long] = db.del(key)

  override def upsert[V](
    key: String,
    value: V,
    expire: Option[Duration]
  )(implicit ev: ByteStringSerializer[V]): Future[Boolean] =
    db.set(key, value)

  override def get(key: String): Future[Option[String]] =
    db.get[String](key)
}
