/*
 * Copyright (c) 2012 - 2017 Splice Machine, Inc.
 *
 * This file is part of Splice Machine.
 * Splice Machine is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License as published by the Free Software Foundation, either
 * version 3, or (at your option) any later version.
 * Splice Machine is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with Splice Machine.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package com.splicemachine.spark.splicemachine

import java.math.BigDecimal
import java.sql.{Time, Timestamp}
import java.util.Date
import org.apache.spark.sql.execution.datasources.jdbc.{JDBCOptions, JdbcUtils}
import org.apache.spark.{SparkConf, SparkContext}
import org.scalatest.{BeforeAndAfterAll, Suite}

trait TestContext extends BeforeAndAfterAll { self: Suite =>

  var sc: SparkContext = null
  var splicemachineContext: SplicemachineContext = null
  val table = "test"
  val externalTable = "testExternal"
  val schema = "TestContext"
  val internalTN = schema+"."+table
  val externalTN = schema+"."+externalTable

  val appID = new Date().toString + math.floor(math.random * 10E4).toLong.toString
  val defaultJDBCURL = "jdbc:splice://localhost:1527/splicedb;create=true;user=splice;password=admin"
  val allTypesCreateStringWithPrimaryKey = "(" +
    "c1_boolean boolean, " +
    "c2_char char(5), " +
    "c3_date date, " +
    "c4_decimal numeric(15,2), " +
    "c5_double double, " +
    "c6_int int, " +
    "c7_bigint bigint, " +
    "c8_float float, " +
    "c9_smallint smallint, " +
    "c10_time time, " +
    "c11_timestamp timestamp, " +
    "c12_varchar varchar(56), " +
    "primary key (c6_int, c7_bigint)" +
     ")"
  val allTypesCreateStringWithoutPrimaryKey = "(" +
    "c1_boolean boolean, " +
    "c2_char char(5), " +
    "c3_date date, " +
    "c4_decimal numeric(15,2), " +
    "c5_double double, " +
    "c6_int int, " +
    "c7_bigint bigint, " +
    "c8_float float, " +
    "c9_smallint smallint, " +
    "c10_time time, " +
    "c11_timestamp timestamp, " +
    "c12_varchar varchar(56)" +
    ")"

  val primaryKeys = Seq("c6_int","c7_bigint")

  val allTypesInsertString = "(" +
    "c1_boolean, " +
    "c2_char, " +
    "c3_date, " +
    "c4_decimal, " +
    "c5_double, " +
    "c6_int, " +
    "c7_bigint, " +
    "c8_float, " +
    "c9_smallint, " +
    "c10_time, " +
    "c11_timestamp, " +
    "c12_varchar " +
    ") "
  val allTypesInsertStringValues = "values (?,?,?,?,?,?,?,?,?,?,?,?)"

  val primaryKeyDelete = "where c6_int = ? and c7_bigint = ?"


  val internalOptions = Map(
    JDBCOptions.JDBC_TABLE_NAME -> internalTN,
    JDBCOptions.JDBC_URL -> defaultJDBCURL
  )

  val externalOptions = Map(
    JDBCOptions.JDBC_TABLE_NAME -> externalTN,
    JDBCOptions.JDBC_URL -> defaultJDBCURL
  )

  val internalJDBCOptions = new JDBCOptions(internalOptions)

  val externalJDBCOptions = new JDBCOptions(externalOptions)

  val conf = new SparkConf().
    setMaster("local[*]").
    setAppName("test").
    set("spark.ui.enabled", "false").
    set("spark.app.id", appID)

  override def beforeAll() {
    sc = new SparkContext(conf)
    splicemachineContext = new SplicemachineContext()
  }

  override def afterAll() {
    if (sc != null) sc.stop()
  }

  def deleteInternalRow(key: Int): Unit = {
    val conn = JdbcUtils.createConnectionFactory(internalJDBCOptions)()
    try {
      val ps = conn.prepareStatement(primaryKeyDelete)
      ps.setInt(1,key)
      ps.setLong(2,key)
      ps.executeUpdate()
    } finally {
      conn.close()
    }
  }

  /**
    *
    * Insert Splice Machine Row
    *
    * @param rowCount rows to return
    * @return
    */
  def insertInternalRows(rowCount: Integer): Unit = {
      val conn = JdbcUtils.createConnectionFactory(internalJDBCOptions)()
      if (!splicemachineContext.tableExists(internalTN))
        conn.createStatement().execute("create table "+internalTN + this.allTypesCreateStringWithPrimaryKey)
      try {
        Range(0, rowCount).map { i =>
          val ps = conn.prepareStatement("insert into " + internalTN + allTypesInsertString + allTypesInsertStringValues)
          ps.setBoolean(1, i % 2==0)
          ps.setString(2, if (i < 8)"" + i else null)
          ps.setDate(3, if (i % 2==0) java.sql.Date.valueOf("2013-09-04") else java.sql.Date.valueOf("2013-09-05"))
          ps.setBigDecimal(4, new BigDecimal("" + i))
          ps.setDouble(5, i)
          ps.setInt(6, i)
          ps.setInt(7, i)
          ps.setFloat(8, i)
          ps.setShort(9, i.toShort)
          ps.setTime(10, new Time(i))
          ps.setTimestamp(11, new Timestamp(i))
          ps.setString(12, if (i < 8) "sometestinfo" + i else null)
          ps.execute()
        }
      }finally {
        conn.close()
      }
  }
}