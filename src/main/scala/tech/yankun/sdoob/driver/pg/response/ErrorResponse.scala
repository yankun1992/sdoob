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

class ErrorResponse extends PGResponse {
  override def toString: String = {
    "ErrorResponse{" + "severity='" + getSeverity + '\'' + ", code='" + getCode + '\'' + ", message='" +
      getMessage + '\'' + ", detail='" + getDetail + '\'' + ", hint='" + getHint + '\'' + ", position='" +
      getPosition + '\'' + ", internalPosition='" + getInternalPosition + '\'' + ", internalQuery='" +
      getInternalQuery + '\'' + ", where='" + getWhere + '\'' + ", file='" + getFile + '\'' + ", line='" +
      getLine + '\'' + ", routine='" + getRoutine + '\'' + ", schema='" + getSchema + '\'' + ", table='" +
      getTable + '\'' + ", column='" + getColumn + '\'' + ", dataType='" + getDataType + '\'' + ", constraint='" +
      getConstraint + '\'' + '}'
  }

  def toException: PGException = new PGException(toString)

}
