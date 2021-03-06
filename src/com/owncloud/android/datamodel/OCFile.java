/* ownCloud Android client application
 *   Copyright (C) 2012  Bartek Przybylski
 *   Copyright (C) 2012-2013 ownCloud Inc.
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.owncloud.android.datamodel;

import java.io.File;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class OCFile implements Parcelable, Comparable<OCFile> {

    public static final Parcelable.Creator<OCFile> CREATOR = new Parcelable.Creator<OCFile>() {
        @Override
        public OCFile createFromParcel(Parcel source) {
            return new OCFile(source);
        }

        @Override
        public OCFile[] newArray(int size) {
            return new OCFile[size];
        }
    };

    public static final String PATH_SEPARATOR = "/";

    private static final String TAG = OCFile.class.getSimpleName();
    
    private long mId;
    private long mParentId;
    private long mLength;
    private long mCreationTimestamp;
    private long mModifiedTimestamp;
    private long mModifiedTimestampAtLastSyncForData;
    private String mRemotePath;
    private String mLocalPath;
    private String mMimeType;
    private boolean mNeedsUpdating;
    private long mLastSyncDateForProperties;
    private long mLastSyncDateForData;
    private boolean mKeepInSync;

    private String mEtag;

    /**
     * Create new {@link OCFile} with given path.
     * 
     * The path received must be URL-decoded. Path separator must be OCFile.PATH_SEPARATOR, and it must be the first character in 'path'.
     * 
     * @param path The remote path of the file.
     */
    public OCFile(String path) {
        resetData();
        mNeedsUpdating = false;
        if (path == null || path.length() <= 0 || !path.startsWith(PATH_SEPARATOR)) {
            throw new IllegalArgumentException("Trying to create a OCFile with a non valid remote path: " + path);
        }
        mRemotePath = path;
    }

    /**
     * Reconstruct from parcel
     * 
     * @param source The source parcel
     */
    private OCFile(Parcel source) {
        mId = source.readLong();
        mParentId = source.readLong();
        mLength = source.readLong();
        mCreationTimestamp = source.readLong();
        mModifiedTimestamp = source.readLong();
        mModifiedTimestampAtLastSyncForData = source.readLong();
        mRemotePath = source.readString();
        mLocalPath = source.readString();
        mMimeType = source.readString();
        mNeedsUpdating = source.readInt() == 0;
        mKeepInSync = source.readInt() == 1;
        mLastSyncDateForProperties = source.readLong();
        mLastSyncDateForData = source.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeLong(mParentId);
        dest.writeLong(mLength);
        dest.writeLong(mCreationTimestamp);
        dest.writeLong(mModifiedTimestamp);
        dest.writeLong(mModifiedTimestampAtLastSyncForData);
        dest.writeString(mRemotePath);
        dest.writeString(mLocalPath);
        dest.writeString(mMimeType);
        dest.writeInt(mNeedsUpdating ? 1 : 0);
        dest.writeInt(mKeepInSync ? 1 : 0);
        dest.writeLong(mLastSyncDateForProperties);
        dest.writeLong(mLastSyncDateForData);
    }
    
    /**
     * Gets the ID of the file
     * 
     * @return the file ID
     */
    public long getFileId() {
        return mId;
    }

    /**
     * Returns the remote path of the file on ownCloud
     * 
     * @return The remote path to the file
     */
    public String getRemotePath() {
        return mRemotePath;
    }

    /**
     * Can be used to check, whether or not this file exists in the database
     * already
     * 
     * @return true, if the file exists in the database
     */
    public boolean fileExists() {
        return mId != -1;
    }

    /**
     * Use this to find out if this file is a Directory
     * 
     * @return true if it is a directory
     */
    public boolean isDirectory() {
        return mMimeType != null && mMimeType.equals("DIR");
    }

    /**
     * Use this to check if this file is available locally
     * 
     * @return true if it is
     */
    public boolean isDown() {
        if (mLocalPath != null && mLocalPath.length() > 0) {
            File file = new File(mLocalPath);
            return (file.exists());
        }
        return false;
    }
    
    /**
     * The path, where the file is stored locally
     * 
     * @return The local path to the file
     */
    public String getStoragePath() {
        return mLocalPath;
    }

    /**
     * Can be used to set the path where the file is stored
     * 
     * @param storage_path to set
     */
    public void setStoragePath(String storage_path) {
        mLocalPath = storage_path;
    }

    /**
     * Get a UNIX timestamp of the file creation time
     * 
     * @return A UNIX timestamp of the time that file was created
     */
    public long getCreationTimestamp() {
        return mCreationTimestamp;
    }

    /**
     * Set a UNIX timestamp of the time the file was created
     * 
     * @param creation_timestamp to set
     */
    public void setCreationTimestamp(long creation_timestamp) {
        mCreationTimestamp = creation_timestamp;
    }

    /**
     * Get a UNIX timestamp of the file modification time.
     *
     * @return  A UNIX timestamp of the modification time, corresponding to the value returned by the server
     *          in the last synchronization of the properties of this file. 
     */
    public long getModificationTimestamp() {
        return mModifiedTimestamp;
    }

    /**
     * Set a UNIX timestamp of the time the time the file was modified.
     * 
     * To update with the value returned by the server in every synchronization of the properties 
     * of this file.
     * 
     * @param modification_timestamp to set
     */
    public void setModificationTimestamp(long modification_timestamp) {
        mModifiedTimestamp = modification_timestamp;
    }

    
    /**
     * Get a UNIX timestamp of the file modification time.
     *
     * @return  A UNIX timestamp of the modification time, corresponding to the value returned by the server
     *          in the last synchronization of THE CONTENTS of this file. 
     */
    public long getModificationTimestampAtLastSyncForData() {
        return mModifiedTimestampAtLastSyncForData;
    }

    /**
     * Set a UNIX timestamp of the time the time the file was modified.
     * 
     * To update with the value returned by the server in every synchronization of THE CONTENTS 
     * of this file.
     * 
     * @param modification_timestamp to set
     */
    public void setModificationTimestampAtLastSyncForData(long modificationTimestamp) {
        mModifiedTimestampAtLastSyncForData = modificationTimestamp;
    }

    
    
    /**
     * Returns the filename and "/" for the root directory
     * 
     * @return The name of the file
     */
    public String getFileName() {
        File f = new File(getRemotePath());
        return f.getName().length() == 0 ? PATH_SEPARATOR : f.getName();
    }
    
    /**
     * Sets the name of the file
     * 
     * Does nothing if the new name is null, empty or includes "/" ; or if the file is the root directory 
     */
    public void setFileName(String name) {
        Log.d(TAG, "OCFile name changin from " + mRemotePath);
        if (name != null && name.length() > 0 && !name.contains(PATH_SEPARATOR) && !mRemotePath.equals(PATH_SEPARATOR)) {
            String parent = (new File(getRemotePath())).getParent();
            parent = (parent.endsWith(PATH_SEPARATOR)) ? parent : parent + PATH_SEPARATOR;
            mRemotePath =  parent + name;
            if (isDirectory()) {
                mRemotePath += PATH_SEPARATOR;
            }
            Log.d(TAG, "OCFile name changed to " + mRemotePath);
        }
    }

    /**
     * Can be used to get the Mimetype
     * 
     * @return the Mimetype as a String
     */
    public String getMimetype() {
        return mMimeType;
    }

    /**
     * Adds a file to this directory. If this file is not a directory, an
     * exception gets thrown.
     * 
     * @param file to add
     * @throws IllegalStateException if you try to add a something and this is
     *             not a directory
     */
    public void addFile(OCFile file) throws IllegalStateException {
        if (isDirectory()) {
            file.mParentId = mId;
            mNeedsUpdating = true;
            return;
        }
        throw new IllegalStateException(
                "This is not a directory where you can add stuff to!");
    }

    /**
     * Used internally. Reset all file properties
     */
    private void resetData() {
        mId = -1;
        mRemotePath = null;
        mParentId = 0;
        mLocalPath = null;
        mMimeType = null;
        mLength = 0;
        mCreationTimestamp = 0;
        mModifiedTimestamp = 0;
        mModifiedTimestampAtLastSyncForData = 0;
        mLastSyncDateForProperties = 0;
        mLastSyncDateForData = 0;
        mKeepInSync = false;
        mNeedsUpdating = false;
    }

    /**
     * Sets the ID of the file
     * 
     * @param file_id to set
     */
    public void setFileId(long file_id) {
        mId = file_id;
    }

    /**
     * Sets the Mime-Type of the
     * 
     * @param mimetype to set
     */
    public void setMimetype(String mimetype) {
        mMimeType = mimetype;
    }

    /**
     * Sets the ID of the parent folder
     * 
     * @param parent_id to set
     */
    public void setParentId(long parent_id) {
        mParentId = parent_id;
    }

    /**
     * Sets the file size in bytes
     * 
     * @param file_len to set
     */
    public void setFileLength(long file_len) {
        mLength = file_len;
    }

    /**
     * Returns the size of the file in bytes
     * 
     * @return The filesize in bytes
     */
    public long getFileLength() {
        return mLength;
    }

    /**
     * Returns the ID of the parent Folder
     * 
     * @return The ID
     */
    public long getParentId() {
        return mParentId;
    }

    /**
     * Check, if this file needs updating
     * 
     * @return
     */
    public boolean needsUpdatingWhileSaving() {
        return mNeedsUpdating;
    }
    
    public long getLastSyncDateForProperties() {
        return mLastSyncDateForProperties;
    }
    
    public void setLastSyncDateForProperties(long lastSyncDate) {
        mLastSyncDateForProperties = lastSyncDate;
    }
    
    public long getLastSyncDateForData() {
        return mLastSyncDateForData;
    }

    public void setLastSyncDateForData(long lastSyncDate) {
        mLastSyncDateForData = lastSyncDate;
    }

    public void setKeepInSync(boolean keepInSync) {
        mKeepInSync = keepInSync;
    }
    
    public boolean keepInSync() {
        return mKeepInSync;
    }
    
    @Override
    public int describeContents() {
        return this.hashCode();
    }

    @Override
    public int compareTo(OCFile another) {
        if (isDirectory() && another.isDirectory()) {
            return getRemotePath().toLowerCase().compareTo(another.getRemotePath().toLowerCase());
        } else if (isDirectory()) {
            return -1;
        } else if (another.isDirectory()) {
            return 1;
        }
        return getRemotePath().toLowerCase().compareTo(another.getRemotePath().toLowerCase());
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof OCFile){
            OCFile that = (OCFile) o;
            if(that != null){
                return this.mId == that.mId;
            }
        }
        
        return false;
    }

    @Override
    public String toString() {
        String asString = "[id=%s, name=%s, mime=%s, downloaded=%s, local=%s, remote=%s, parentId=%s, keepInSinc=%s]";
        asString = String.format(asString, Long.valueOf(mId), getFileName(), mMimeType, isDown(), mLocalPath, mRemotePath, Long.valueOf(mParentId), Boolean.valueOf(mKeepInSync));
        return asString;
    }

    public String getEtag() {
        return mEtag;
    }

    public long getLocalModificationTimestamp() {
        if (mLocalPath != null && mLocalPath.length() > 0) {
            File f = new File(mLocalPath);
            return f.lastModified();
        }
        return 0;
    }

}
