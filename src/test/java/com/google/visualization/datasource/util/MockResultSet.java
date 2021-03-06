// Copyright 2009 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.visualization.datasource.util;

import java.io.InputStream;
import java.io.Reader;

import java.math.BigDecimal;

import java.net.URL;

import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * A mock result set for SqlDataSource unit tests.
 *
 * @author Liron L.
 */
public class MockResultSet implements ResultSet {

  /**
   * List of the table rows
   * */
  private final List<List<Object>> rows;

  /**
   * The number of columns.
   */
  private final int numOfCols;

  /**
   * Column labels.
   */
  List<String> labels;

  /**
   * Column Types.
   */
  List<Integer> types;

  /**
   * Current row index.
   */
  private int rowIndex;

  // An indication whether the last column read had a value of SQL NULL.
  private boolean wasNull;

  /**
   * Constructor.
   *
   * @param rows List of the table rows.
   * @param numOfCols The number of columns.
   * @param labels Column labels.
   * @param types Column types.
   */
  public MockResultSet(List<List<Object>> rows, int numOfCols,
      List<String> labels, List<Integer> types) {
    this.rows = rows;
    this.numOfCols = numOfCols;
    this.labels = labels;
    this.types = types;
    this.rowIndex = -1;
    this.wasNull = false;
  }

  /**
   * Retrieves the value of the designated column in the current row
   * as an object.
   *
   * @param columnIndex The column index. SQL indexes are 1-based.
   *
   * @return The column Object.
   *
   * @throws SQLException Thrown when the column index is out of bounds.
   */
  private Object getObjectFromCell(int columnIndex) throws SQLException {
    if (columnIndex > numOfCols) {
      throw new SQLException("The index column is out of bounds. Index = "
          + columnIndex + ", number of rows = " + numOfCols);
    }

    Object obj = rows.get(rowIndex).get(columnIndex - 1);
    wasNull = (obj == null);
    return obj;
  }

  /**
   * Moves the cursor forward one row from its current position.
   * The cursor is initially positioned before the first row; the first call to
   * the method <code>next</code> makes the first row the current row; the
   * second call makes the second row the current row, and so on.
   * <p>
   * When a call to the <code>next</code> method returns <code>false</code>,
   * the cursor is positioned after the last row.
   *
   * @return <code>true</code> if the new current row is valid;
   * <code>false</code> if there are no more rows.
   */
  @Override
  public boolean next() {
    rowIndex++;
    return (rowIndex < rows.size());
  }

  @Override
  public void close() {
    throw new UnsupportedOperationException("This operation unsupported.");
  }

  /**
   * Reports whether the last column read had a value of SQL <code>NULL</code>.
   *
   * @return <code>true</code> if the last column value read was SQL
   *         <code>NULL</code> and <code>false</code> otherwise
   */
  @Override
  public boolean wasNull() {
    return wasNull;
  }

  /**
   * Retrieves the value of the designated column in the current row
   * as a string.
   *
   * @param columnIndex The column index. SQL indexes are 1-based.
   *
   * @return the column value; if the value is SQL <code>NULL</code>, the
   * value returned is <code>null</code>
   *
   * @throws SQLException Thrown when the column index is out of bounds.
   */
  @Override
  public String getString(int columnIndex) throws SQLException {
    Object str = getObjectFromCell(columnIndex);
    if (!wasNull) {
      // Use the valueOf method (insr=tead of casting) for cases where str
      // is a character object.
      return String.valueOf(str);
    }
    return null;
  }

  /**
   * Retrieves the value of the designated column in the current row
   * as a boolean.
   *
   * @param columnIndex The column index. SQL indexes are 1-based.
   *
   * @return The column value; if the value is SQL <code>NULL</code>, the
   *     value returned is <code>false</code>
   *
   * @throws SQLException Thrown when the column index is out of bounds.
   */
  @Override
  public boolean getBoolean(int columnIndex) throws SQLException {
    Object bool = getObjectFromCell(columnIndex);
    return ((!wasNull) && (Boolean) bool);
  }

  @Override
  public byte getByte(int columnIndex) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public short getShort(int columnIndex) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public int getInt(int columnIndex) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public long getLong(int columnIndex) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public float getFloat(int columnIndex) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  /**
   * Retrieves the value of the designated column in the current row
   * as a double. Supports only cases where the original object is an Integer
   * or a Double.
   *
   * @param columnIndex The column index. SQL indexes are 1-based.
   *
   * @return The column value; if the value is SQL <code>NULL</code>,
   * the value returned is <code>0</code>.
   *
   * @throws SQLException Thrown when the column index is out of bounds.
   */
  @Override
  public double getDouble(int columnIndex) throws SQLException {
    Object d = getObjectFromCell(columnIndex);
    if (!wasNull) {
      if (d instanceof Integer) {
        return (Integer) d;
      } else {
        return (Double) d;
      }
    }
    return 0;
  }

  @Override
  public BigDecimal getBigDecimal(int columnIndex, int scale) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public byte[] getBytes(int columnIndex) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  /**
   * Retrieves the value of the designated column in the current row
   * as a Date object.
   *
   * @param columnIndex The column index. SQL indexes are 1-based.
   *
   * @return The column value; if the value is SQL <code>NULL</code>, the
   * value returned is <code>null</code>
   *
   * @throws SQLException Thrown when the column index is out of bounds.
   */
  @Override
  public Date getDate(int columnIndex) throws SQLException {
    Object date = getObjectFromCell(columnIndex);
    if (!wasNull) {
      return (Date) date;
    }
    return null;
  }

  /**
   * Retrieves the value of the designated column in the current row
   * as a Time object.
   *
   * @param columnIndex The column index. SQL indexes are 1-based.
   *
   * @return The column value; if the value is SQL <code>NULL</code>, the
   *     value returned is <code>null</code>.
   *
   * @throws SQLException Thrown when the column index is out of bounds.
   */
  @Override
  public Time getTime(int columnIndex) throws SQLException {
    Object time  = this.getObjectFromCell(columnIndex);
    if (!wasNull) {
      return (Time) time;
    }
    return null;
  }

  /**
   * Retrieves the value of the designated column in the current row
   * as a Timestamp object.
   *
   * @param columnIndex The column index. SQL indexes are 1-based.
   *
   * @return The column value; if the value is SQL <code>NULL</code>, the
   *     value returned is <code>null</code>.
   *
   * @throws SQLException Thrown when the column index is out of bounds.
   */
  @Override
  public Timestamp getTimestamp(int columnIndex) throws SQLException {
    Object timestamp  = this.getObjectFromCell(columnIndex);
    if (!wasNull) {
      return (Timestamp) timestamp;
    }
    return null;
  }

  @Override
  public InputStream getAsciiStream(int columnIndex) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public InputStream getUnicodeStream(int columnIndex) throws SQLException {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public InputStream getBinaryStream(int columnIndex) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public String getString(String columnLabel) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public boolean getBoolean(String columnLabel) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public byte getByte(String columnLabel) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public short getShort(String columnLabel) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public int getInt(String columnLabel) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public long getLong(String columnLabel) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public float getFloat(String columnLabel) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public double getDouble(String columnLabel) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public BigDecimal getBigDecimal(String columnLabel, int scale) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public byte[] getBytes(String columnLabel) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public Date getDate(String columnLabel) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public Time getTime(String columnLabel) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public Timestamp getTimestamp(String columnLabel) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public InputStream getAsciiStream(String columnLabel) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public InputStream getUnicodeStream(String columnLabel) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public InputStream getBinaryStream(String columnLabel) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public SQLWarning getWarnings() {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void clearWarnings() {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public String getCursorName() {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  /**
   * Retrieves the number, types and properties of the columns.
   * The data is stored in a mock ResultSetMetaData.
   *
   * @return the description of this <code>ResultSet</code> object's columns.
   */
  @Override
  public ResultSetMetaData getMetaData() {
    return new MockResultSetMetaData(numOfCols, labels, types);
  }

  @Override
  public Object getObject(int columnIndex) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public Object getObject(String columnLabel) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public int findColumn(String columnLabel) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public Reader getCharacterStream(int columnIndex) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public Reader getCharacterStream(String columnLabel) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public BigDecimal getBigDecimal(int columnIndex) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public BigDecimal getBigDecimal(String columnLabel) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public boolean isBeforeFirst() {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public boolean isAfterLast() {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public boolean isFirst() {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public boolean isLast() {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void beforeFirst() {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void afterLast() {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public boolean first() {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public boolean last() {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public int getRow() {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public boolean absolute(int r) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public boolean relative(int r) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public boolean previous() {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void setFetchDirection(int direction) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public int getFetchDirection() {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void setFetchSize(int r) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public int getFetchSize() {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public int getType() {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public int getConcurrency() {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public boolean rowUpdated() {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public boolean rowInserted() {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public boolean rowDeleted() {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateNull(int columnIndex) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateBoolean(int columnIndex, boolean x) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateByte(int columnIndex, byte x) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateShort(int columnIndex, short x) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateInt(int columnIndex, int x) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateLong(int columnIndex, long x) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateFloat(int columnIndex, float x) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateDouble(int columnIndex, double x) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateBigDecimal(int columnIndex, BigDecimal x) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateString(int columnIndex, String x) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateBytes(int columnIndex, byte x[]) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateDate(int columnIndex, Date x) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateTime(int columnIndex, Time x) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateTimestamp(int columnIndex, Timestamp x) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateAsciiStream(int columnIndex, InputStream x, int length) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateBinaryStream(int columnIndex, InputStream x, int length) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateCharacterStream(int columnIndex, Reader x, int length) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateObject(int columnIndex, Object x, int scaleOrLength) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateObject(int columnIndex, Object x) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateNull(String columnLabel) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateBoolean(String columnLabel, boolean x) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateByte(String columnLabel, byte x) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateShort(String columnLabel, short x) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateInt(String columnLabel, int x) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateLong(String columnLabel, long x) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateFloat(String columnLabel, float x) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateDouble(String columnLabel, double x) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateBigDecimal(String columnLabel, BigDecimal x) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateString(String columnLabel, String x) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateBytes(String columnLabel, byte x[]) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateDate(String columnLabel, Date x) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateTime(String columnLabel, Time x) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateTimestamp(String columnLabel, Timestamp x) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateAsciiStream(String columnLabel, InputStream x, int length) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateBinaryStream(String columnLabel, InputStream x, int length) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateCharacterStream(String columnLabel, Reader reader,
      int length) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateObject(String columnLabel, Object x, int scaleOrLength) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateObject(String columnLabel, Object x) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void insertRow() {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateRow() {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void deleteRow() {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }
  
  @Override
  public void refreshRow() {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void cancelRowUpdates() {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void moveToInsertRow() {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void moveToCurrentRow() {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public Statement getStatement() {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public Object getObject(int columnIndex, Map<String, Class<?>> map) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public Ref getRef(int columnIndex) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public Blob getBlob(int columnIndex) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public Clob getClob(int columnIndex) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public Array getArray(int columnIndex) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public Object getObject(String columnLabel, Map<String, Class<?>> map) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public Ref getRef(String columnLabel) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public Blob getBlob(String columnLabel) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public Clob getClob(String columnLabel) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public Array getArray(String columnLabel) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public Date getDate(int columnIndex, Calendar cal) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public Date getDate(String columnLabel, Calendar cal) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public Time getTime(int columnIndex, Calendar cal) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public Time getTime(String columnLabel, Calendar cal) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public Timestamp getTimestamp(int columnIndex, Calendar cal) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public Timestamp getTimestamp(String columnLabel, Calendar cal) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public URL getURL(int columnIndex) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public URL getURL(String columnLabel) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateRef(int columnIndex, Ref x) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateRef(String columnLabel, Ref x) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateBlob(int columnIndex, Blob x) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateBlob(String columnLabel, Blob x) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateClob(int columnIndex, Clob x) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateClob(String columnLabel, Clob x) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateArray(int columnIndex, Array x) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateArray(String columnLabel, Array x) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public RowId getRowId(int columnIndex) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public RowId getRowId(String columnLabel) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateRowId(int columnIndex, RowId x) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateRowId(String columnLabel, RowId x) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  public int getHoldability() {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public boolean isClosed() {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateNString(int columnIndex, String nString) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateNString(String columnLabel, String nString) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateNClob(int columnIndex, NClob nClob) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateNClob(String columnLabel, NClob nClob) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public NClob getNClob(int columnIndex) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public NClob getNClob(String columnLabel) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public SQLXML getSQLXML(int columnIndex) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public SQLXML getSQLXML(String columnLabel) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateSQLXML(int columnIndex, SQLXML xmlObject) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateSQLXML(String columnLabel, SQLXML xmlObject) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public String getNString(int columnIndex) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public String getNString(String columnLabel) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public Reader getNCharacterStream(int columnIndex) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public Reader getNCharacterStream(String columnLabel) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateNCharacterStream(int columnIndex, Reader x, long length) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateNCharacterStream(String columnLabel, Reader reader,
      long length) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateAsciiStream(int columnIndex, InputStream x, long length) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateBinaryStream(int columnIndex, InputStream x, long length) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateCharacterStream(int columnIndex, Reader x, long length) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateAsciiStream(String columnLabel, InputStream x, long length) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateBinaryStream(String columnLabel, InputStream x, long length) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateCharacterStream(String columnLabel, Reader reader,
      long length) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateBlob(int columnIndex, InputStream inputStream, long length) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateBlob(String columnLabel, InputStream inputStream,
      long length) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateClob(int columnIndex, Reader reader, long length) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateClob(String columnLabel, Reader reader, long length) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateNClob(int columnIndex, Reader reader, long length) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }
  
  @Override
  public void updateNClob(String columnLabel, Reader reader, long length) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateNCharacterStream(int columnIndex, Reader x) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateNCharacterStream(String columnLabel, Reader reader) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateAsciiStream(int columnIndex, InputStream x) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateBinaryStream(int columnIndex, InputStream x) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateCharacterStream(int columnIndex, Reader x) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateAsciiStream(String columnLabel, InputStream x) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateBinaryStream(String columnLabel, InputStream x) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateCharacterStream(String columnLabel, Reader reader) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateBlob(int columnIndex, InputStream inputStream) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateBlob(String columnLabel, InputStream inputStream) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateClob(int columnIndex, Reader reader) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateClob(String columnLabel, Reader reader) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateNClob(int columnIndex, Reader reader) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public void updateNClob(String columnLabel, Reader reader) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public <T> T unwrap(Class<T> iface) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }

  @Override
  public boolean isWrapperFor(Class<?> iface) {
    throw new UnsupportedOperationException("This operation is unsupported.");
  }
  
  @Override
  public <T> T getObject(String string, Class<T> iface) {
      throw new UnsupportedOperationException("This operation is unsupported.");
  }
  
  @Override
  public <T> T getObject(int integer, Class<T> iface) {
      throw new UnsupportedOperationException("This operation is unsupported.");
  }
}
