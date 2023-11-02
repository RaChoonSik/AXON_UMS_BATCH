package kr.co.enders.crypto.seed;

/**
 * ��ȣȭ���� �? ����� ���߱� ���� ���Ǵ� Padding�� �߻�ȭ �� Interface
 * 
 */



public interface CryptoPadding {

    /**
     * ��û�� Block Size�� ���߱� ���� Padding�� �߰��Ѵ�.
     * 
     * @param source
     *            byte[] �е��� �߰��� bytes
     * @param blockSize
     *            int block size
     * @return byte[] �е��� �߰� �� ���?bytes
     */
    public byte[] addPadding(byte[] source, int blockSize);

    /**
     * ��û�� Block Size�� ���߱� ���� �߰� �� Padding�� �����Ѵ�.
     * 
     * @param source
     *            byte[] �е��� ������ bytes
     * @param blockSize
     *            int block size
     * @return byte[] �е��� ���� �� ���?bytes
     */
    public byte[] removePadding(byte[] source, int blockSize);

}
