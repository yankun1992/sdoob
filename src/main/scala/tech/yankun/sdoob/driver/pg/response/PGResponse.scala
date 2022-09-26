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

package tech.yankun.sdoob.driver.pg.response

import scala.beans.BeanProperty

abstract class PGResponse {
  @BeanProperty var severity: String = _
  @BeanProperty var code: String = _
  @BeanProperty var message: String = _
  @BeanProperty var detail: String = _
  @BeanProperty var hint: String = _
  @BeanProperty var position: String = _
  @BeanProperty var internalPosition: String = _
  @BeanProperty var internalQuery: String = _
  @BeanProperty var where: String = _
  @BeanProperty var file: String = _
  @BeanProperty var line: String = _
  @BeanProperty var routine: String = _
  @BeanProperty var schema: String = _
  @BeanProperty var table: String = _
  @BeanProperty var column: String = _
  @BeanProperty var dataType: String = _
  @BeanProperty var constraint: String = _
}
