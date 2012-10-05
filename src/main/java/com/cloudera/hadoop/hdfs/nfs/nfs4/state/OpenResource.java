/**
 * Copyright 2012 The Apache Software Foundation
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.cloudera.hadoop.hdfs.nfs.nfs4.state;

import java.io.Closeable;
import java.io.IOException;

import com.cloudera.hadoop.hdfs.nfs.nfs4.StateID;

/**
 * Class represents an open input/output stream internally to the
 * NFS4Handler class.
 */
public class OpenResource<T extends Closeable> implements Closeable {

  private final HDFSFile mHDFSFile;
  private final T mResource;
  private final StateID mStateID;
  private boolean mConfirmed;
  private long mTimestamp;

  public OpenResource(HDFSFile hdfsFile, StateID stateID, T resource) {
    this.mHDFSFile = hdfsFile;
    this.mStateID = stateID;
    this.mResource = resource;
    mTimestamp = System.currentTimeMillis();
  }

  public void setSequenceID(int seqID) {
    mStateID.setSeqID(seqID);
  }

  public boolean isOwnedBy(StateID stateID) {
    return mStateID.equals(stateID);
  }

  public T get() {
    return mResource;
  }

  @Override
  public void close() throws IOException {
    if (mResource != null) {
      mHDFSFile.removeResource(mResource, mStateID);
      synchronized (mResource) {
        mResource.close();
      }
    }
  }

  public boolean isConfirmed() {
    return mConfirmed;
  }

  public void setConfirmed(boolean confirmed) {
    mConfirmed = confirmed;
  }

  public long getTimestamp() {
    return mTimestamp;
  }

  public void setTimestamp(long timestamp) {
    this.mTimestamp = timestamp;
  }

  public StateID getStateID() {
    return mStateID;
  }

  public HDFSFile getHDFSFile() {
    return mHDFSFile;
  }
}