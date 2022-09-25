/*
 * Copyright (C) 2022  Yan Kun
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package tech.yankun.sdoob.writer

import org.apache.spark.sql.{DataFrame, Row}
import tech.yankun.sdoob.args.AppArgs
import tech.yankun.sdoob.driver.PoolOptions
import tech.yankun.sdoob.driver.mysql.MySQLConnectOptions

class MySQLWriter(connectOptions: MySQLConnectOptions, poolOptions: PoolOptions, appArgs: AppArgs)
  extends Writer(connectOptions, poolOptions, appArgs) {

  override def write(dataset: DataFrame, mode: WriteMode): Unit = {
    val schema = dataset.schema

    dataset.foreachPartition { partition: Iterator[Row] =>

    }
  }
}

object MySQLWriter {
  def apply(connectOptions: MySQLConnectOptions, poolOptions: PoolOptions, appArgs: AppArgs): MySQLWriter =
    new MySQLWriter(connectOptions, poolOptions, appArgs)
}
