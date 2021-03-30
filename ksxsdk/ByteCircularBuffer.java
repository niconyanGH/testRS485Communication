package ksxsdk;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteOrder;
import java.security.InvalidParameterException;


public class ByteCircularBuffer
{
	protected final boolean mIsLittleEndian;
	protected       byte[]  mArray;
	protected       int     mFront;
	protected       int     mRear;
	protected       int     mCount;

	/**
	 * 새 ByteCircularBuffer 를 만든다.
	 *
	 * @param capacity 버퍼의 최대 크기
	 * @param order    버퍼에 값을 읽거나 쓸때, 사용할 byte order 를 지정
	 */
	public ByteCircularBuffer(int capacity, ByteOrder order)
	{
		mArray = new byte[capacity];
		mIsLittleEndian = (ByteOrder.LITTLE_ENDIAN == order);
		clear();
	}

	/**
	 * 버퍼의 크기. 버퍼에 저장되어 있는 byte 수.
	 */
	public int size()
	{
		return mCount;
	}

	/**
	 * 큐에서 지정된 byte 의 인덱스를 찾아 리턴한다.
	 *
	 * @param offset 큐의 rear 로부터 offset. 검색을 시작할 위치이다.
	 * @param value  검색할 값.
	 *
	 * @return 값을 찾으면 해당 인덱스(0 <=)를 리턴한다. 찾지 못하면 -1 을 리턴한다.
	 *
	 * @throws IndexOutOfBoundsException offset 이 큐의 범위를 벗어나면 발생한다.
	 */
	public synchronized int find(int offset, byte value)
	{
		if ((offset < 0) || (mCount <= offset))
		{
			throw new IndexOutOfBoundsException();
		}

		final int count = mCount;
		int       tmpIndex;
		for (int ii = offset; ii < count; ++ii)
		{
			tmpIndex = (mRear + ii) % mArray.length;
			if (value == mArray[tmpIndex])
			{
				return ii;
			}
		}

		return -1;
	}

	/**
	 * 큐에서 지정된 byte[] 의 인덱스를 찾아 리턴한다.
	 *
	 * @param offset 큐의 rear 로부터 offset. 검색을 시작할 위치이다.
	 * @param value  검색할 byte[] 값.
	 *
	 * @return 값을 찾으면 해당 인덱스(0 <=)를 리턴한다. 찾지 못하면 -1 을 리턴한다.
	 *
	 * @throws IndexOutOfRangeException  offset 이 큐의 범위를 벗어나면 발생한다.
	 * @throws InvalidParameterException 파라메터 값이 비정상적이면 발생한다.
	 */
	public synchronized int find(int offset, byte[] value)
	{
		if ((null == value) || (value.length <= 0))
		{
			throw new InvalidParameterException();
		}

		while ((offset + value.length) <= mCount)
		{
			int ret = find(offset, value[0]);
			if (ret < 0)
			{
				return -1;
			}

			if (mCount < ret + value.length)
			{
				return -1;
			}

			int cnt = compare(ret, value);
			if (cnt == value.length)
			{
				return ret;
			}

			offset += cnt;
		}

		return -1;
	}

	/**
	 * 큐에서 지정된 byte[] 와 비교한다.
	 *
	 * @param offset 큐의 rear 로부터 offset. 비교를 시작할 위치이다.
	 * @param value  비교할 byte[] 값.
	 *
	 * @return value 의 크기만큼 비교해서, 값이 동일하면 value 의 크기를 리턴한다. 동일하지 않으면, 순차적으로 비교해서 동일한 원소의 개수를 리턴한다.
	 *
	 * @throws IndexOutOfBoundsException offset 이 큐의 범위를 벗어나면 발생한다.
	 * @throws InvalidParameterException 파라메터 값이 비정상적이면 발생한다.
	 */
	public synchronized int compare(int offset, byte[] value)
	{
		if ((null == value) || (value.length <= 0))
		{
			throw new InvalidParameterException();
		}

		if ((offset < 0) || (mCount < (offset + value.length)))
		{
			throw new IndexOutOfBoundsException();
		}

		int count = value.length;
		int tmpIndex;
		for (int ii = 0; ii < count; ++ii)
		{
			tmpIndex = (mRear + offset + ii) % mArray.length;
			if (value[ii] != mArray[tmpIndex])
			{
				return ii;
			}
		}

		return value.length;
	}

	/**
	 * 큐의 지정한 위치의 byte 값을 리턴한다. rear 를 이동시키지 않는다.
	 *
	 * @param offset 큐의 rear 로부터 offset.
	 *
	 * @return 인덱스 위치의 byte 값.
	 *
	 * @throws IndexOutOfRangeException offset 이 큐의 범위를 벗어나면 발생한다.
	 */
	public synchronized byte get(int offset) throws IndexOutOfBoundsException
	{
		if ((offset < 0) || (mCount <= offset))
		{
			throw new IndexOutOfBoundsException();
		}

		int tmpIndex = (mRear + offset) % mArray.length;
		return mArray[tmpIndex];
	}

	/**
	 * 큐의 지정한 위치에서 2 byte 를 읽어 int 형태로 리턴한다. rear 를 이동시키지 않는다.
	 *
	 * @param offset 큐의 rear 로부터 offset.
	 *
	 * @return 버퍼에서 읽은 short 값.
	 *
	 * @throws IndexOutOfRangeException offset 이 큐의 범위를 벗어나면 발생한다.
	 */
	public synchronized short getShort(int offset) throws IndexOutOfBoundsException
	{
		if (mCount < (offset + 2))
		{
			throw new IndexOutOfBoundsException();
		}

		int tmpIndex = (mRear + offset) % mArray.length;
		int value;
		if (mIsLittleEndian)
		{
			value = (mArray[tmpIndex] & 0xff);
			tmpIndex = (tmpIndex + 1) % mArray.length;

			value = value | ((mArray[tmpIndex] & 0xff) << 8);
		}
		else
		{
			value = ((mArray[tmpIndex] & 0xff) << 8);
			tmpIndex = (tmpIndex + 1) % mArray.length;

			value = value | (mArray[tmpIndex] & 0xff);
		}

		return (short) value;
	}

	/**
	 * 큐의 지정한 위치에서 4 byte 를 읽어 int 형태로 리턴한다. rear 를 이동시키지 않는다.
	 *
	 * @param offset 큐의 rear 로부터 offset.
	 *
	 * @return 버퍼에서 읽은 int 값.
	 *
	 * @throws IndexOutOfRangeException offset 이 큐의 범위를 벗어나면 발생한다.
	 */
	public synchronized int getInt(int offset) throws IndexOutOfBoundsException
	{
		if (mCount < (offset + 4))
		{
			throw new IndexOutOfBoundsException();
		}

		int tmpIndex = (mRear + offset) % mArray.length;
		int value;
		if (mIsLittleEndian)
		{
			value = (mArray[tmpIndex] & 0xff);
			tmpIndex = (tmpIndex + 1) % mArray.length;

			value = value | ((mArray[tmpIndex] & 0xff) << 8);
			tmpIndex = (tmpIndex + 1) % mArray.length;

			value = value | ((mArray[tmpIndex] & 0xff) << 16);
			tmpIndex = (tmpIndex + 1) % mArray.length;

			value = value | ((mArray[tmpIndex] & 0xff) << 24);
			tmpIndex = (tmpIndex + 1) % mArray.length;
		}
		else
		{
			value = ((mArray[tmpIndex] & 0xff) << 24);
			tmpIndex = (tmpIndex + 1) % mArray.length;

			value = value | ((mArray[tmpIndex] & 0xff) << 16);
			tmpIndex = (tmpIndex + 1) % mArray.length;

			value = value | ((mArray[tmpIndex] & 0xff) << 8);
			tmpIndex = (tmpIndex + 1) % mArray.length;

			value = value | (mArray[tmpIndex] & 0xff);
			tmpIndex = (tmpIndex + 1) % mArray.length;
		}

		return value;
	}

	/**
	 * 큐의 rear 에서 지정한 byte 수만큼 읽어온다. rear 를 이동시키지 않는다.
	 *
	 * @param srcOffset 큐의 rear 로부터 인덱스 offset
	 * @param dstArray  값을 복사할 대상 byte array.
	 * @param dstOffset dstArray 의 시작 인덱스.
	 * @param byteCount 읽어올 byte 수.
	 *
	 * @throws IndexOutOfRangeException 읽어올 데이터의 범위가 큐의 범위를 벗어나면 발생한다.
	 */
	public synchronized void getBytes(int srcOffset, byte[] dstArray, int dstOffset, int byteCount) throws IndexOutOfBoundsException
	{

		if ((srcOffset < 0) || mCount < (srcOffset + byteCount))
		{
			throw new IndexOutOfBoundsException();
		}

		int tmpIndex = mRear + srcOffset;

		// 두번에 나누어 복사해야 하는 경우!
		if ((tmpIndex < mArray.length) && (mArray.length < (tmpIndex + byteCount)))
		{
			int splitSize = mArray.length - tmpIndex;
			System.arraycopy(mArray, tmpIndex, dstArray, dstOffset, splitSize);
			System.arraycopy(mArray, 0, dstArray, dstOffset + splitSize, byteCount - splitSize);
		}
		else
		{
			tmpIndex = tmpIndex % mArray.length;

			// 한번에 쭉 복사되는 경우
			System.arraycopy(mArray, tmpIndex, dstArray, dstOffset, byteCount);
		}
	}

	/**
	 * 큐의 rear 에서 byte 값을 꺼내온다. rear 를 1 만큼 이동시킨다.
	 *
	 * @throws BufferUnderflowException 읽어올 값의 크기보다 큐의 크기가 작으면 발생한다.
	 */
	public synchronized byte pop() throws BufferUnderflowException
	{
		if (mCount < 1)
		{
			throw new BufferUnderflowException();
		}

		byte value = mArray[mRear];
		mRear = (mRear + 1) % mArray.length;
		--mCount;
		return value;
	}

	/**
	 * 큐의 rear 에서 int 값을 꺼내온다. rear 를 4 만큼 이동시킨다.
	 *
	 * @throws BufferUnderflowException 읽어올 값의 크기보다 큐의 크기가 작으면 발생한다.
	 */
	public synchronized int popInt() throws BufferUnderflowException
	{
		if (mCount < 4)
		{
			throw new BufferUnderflowException();
		}

		int value = getInt(0); // 맨 앞에서 꺼낸다.
		mRear = (mRear + 4) % mArray.length;
		mCount -= 4;

		return value;
	}

	/**
	 * 큐의 rear 에서 지정한 byte 수만큼 꺼내온다. rear 를 이동시킨다.
	 *
	 * @param dstArray  데이터를 복사할 대상 byte array.
	 * @param dstOffset dstArray 의 시작 offset.
	 * @param byteCount 꺼내올 데이터 byte 수.
	 *
	 * @throws BufferUnderflowException 읽어올 값의 크기보다 큐의 크기가 작으면 발생한다.
	 */
	public synchronized void popBytes(byte[] dstArray, int dstOffset, int byteCount) throws BufferUnderflowException
	{
		if (mCount < byteCount)
		{
			throw new BufferUnderflowException();
		}

		getBytes(0, dstArray, dstOffset, byteCount);

		mRear = (mRear + byteCount) % mArray.length;
		mCount -= byteCount;
	}

	public void clear()
	{
		mFront = mRear = 0;
		mCount = 0;
	}

	/**
	 * 큐의 rear 부터 지정한 만큼의 데이터를 버린다. rear 를 이동시킨다.
	 *
	 * @param byteCount 버릴 데이터 byte 수.
	 *
	 * @throws BufferUnderflowException 버릴 byte 수보다 큐의 크기가 작으면 발생한다.
	 */
	public synchronized void drawBytes(int byteCount) throws BufferUnderflowException
	{
		if (mCount < byteCount)
		{
			throw new BufferUnderflowException();
		}

		mRear = (mRear + byteCount) % mArray.length;
		mCount -= byteCount;

		// Log.d("ByteCircularBuffer", "Front = " + mFront + ", Rear = " + mRear + ", Count = " + mCount);
	}

	/**
	 * 큐의 front 에 byte 값을 쓴다. front 를 1 만큼 이동시킨다.
	 *
	 * @param value 쓸 byte 값.
	 *
	 * @throws BufferOverflowException 버퍼의 크기보다 더 많이 쓰려고 할때 발생한다.
	 */
	public synchronized void push(byte value) throws BufferOverflowException
	{
		if ((mArray.length - mCount) < 1)
		{
			throw new BufferOverflowException();
		}

		mArray[mFront] = value;
		mFront = (mFront + 1) % mArray.length;
		++mCount;
	}

	/**
	 * 큐의 front 에 int 값을 쓴다. front 를 4 만큼 이동시킨다.
	 *
	 * @param value 쓸 int 값.
	 *
	 * @throws BufferOverflowException 버퍼의 크기보다 더 많이 쓰려고 할때 발생한다.
	 */
	public synchronized void pushInt(int value) throws BufferOverflowException
	{
		if ((mArray.length - mCount) < 4)
		{
			throw new BufferOverflowException();
		}

		if (mIsLittleEndian)
		{
			mArray[mFront] = (byte) (value);
			mFront = (mFront + 1) % mArray.length;

			mArray[mFront] = (byte) (value >> 8);
			mFront = (mFront + 1) % mArray.length;

			mArray[mFront] = (byte) (value >> 16);
			mFront = (mFront + 1) % mArray.length;

			mArray[mFront] = (byte) (value >> 24);
			mFront = (mFront + 1) % mArray.length;
		}
		else
		{
			mArray[mFront] = (byte) (value >> 24);
			mFront = (mFront + 1) % mArray.length;

			mArray[mFront] = (byte) (value >> 16);
			mFront = (mFront + 1) % mArray.length;

			mArray[mFront] = (byte) (value >> 8);
			mFront = (mFront + 1) % mArray.length;

			mArray[mFront] = (byte) (value);
			mFront = (mFront + 1) % mArray.length;
		}
		mCount += 4;
	}

	public synchronized void pushBytes(byte[] srcArray, int srcOffset, int byteCount) throws BufferOverflowException
	{
		if ((mArray.length - mCount) < byteCount)
		{
			throw new BufferOverflowException();
		}

		// 한번에 쭉 복사되는 경우
		if (mFront + byteCount <= mArray.length)
		{
			System.arraycopy(srcArray, srcOffset, mArray, mFront, byteCount);
		}
		else
		{
			// 두번에 나누어 복사해야 하는 경우!
			final int splitSize = mArray.length - mFront;
			System.arraycopy(srcArray, srcOffset, mArray, mFront, splitSize);
			System.arraycopy(srcArray, srcOffset + splitSize, mArray, 0, byteCount - splitSize);
		}

		mFront = (mFront + byteCount) % mArray.length;
		mCount += byteCount;

		// Log.d("ByteCircularBuffer", "Front = " + mFront + ", Rear = " + mRear + ", Count = " + mCount);
	}
}
