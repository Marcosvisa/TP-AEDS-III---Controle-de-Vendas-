package util;

public interface Registro {
    public int getId();
    public void setId(int id);
    public byte[] toByteArray() throws Exception;
    public void fromByteArray(byte[] ba) throws Exception;
}
