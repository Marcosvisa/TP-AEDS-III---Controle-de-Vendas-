package dao;
import java.io.*;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import util.Registro;

public class Arquivo<T extends Registro> {

    private RandomAccessFile arquivo;
    private Constructor<T> construtor;
    private String nomeArquivo;

    public Arquivo(String nomeArquivo, Class<T> classe) throws Exception {
        this.nomeArquivo = nomeArquivo;
        this.construtor = classe.getDeclaredConstructor();

        File file = new File(this.nomeArquivo);
        File diretorioPai = file.getParentFile();

        if (diretorioPai != null && !diretorioPai.exists()) {
            if (!diretorioPai.mkdirs()) {
                throw new IOException("Falha ao criar o diretório: " + diretorioPai.getAbsolutePath());
            }
        }

        this.arquivo = new RandomAccessFile(this.nomeArquivo, "rw");

        if (this.arquivo.length() < 4)
            this.arquivo.writeInt(0); //cabeçalho com último id
    }

    public int create(T obj) throws Exception {
        arquivo.seek(0);
        int ultimoID = arquivo.readInt();
        int novoID = ultimoID + 1;
        arquivo.seek(0);
        arquivo.writeInt(novoID);
        obj.setId(novoID);

        arquivo.seek(arquivo.length());
        byte[] ba = obj.toByteArray();
        arquivo.writeChar(' '); //lápide ativa
        arquivo.writeInt(ba.length);
        arquivo.write(ba);
        return novoID;
    }

    public T read(int id) throws Exception {
        arquivo.seek(4);
        while (arquivo.getFilePointer() < arquivo.length()) {
            char lapide = arquivo.readChar();
            int tam = arquivo.readInt();
            byte[] ba = new byte[tam];
            arquivo.read(ba);

            if (lapide == ' ') {
                T obj = construtor.newInstance();
                obj.fromByteArray(ba);
                if (obj.getId() == id)
                    return obj;
            }
        }
        return null;
    }

    //buscar cpf
    public T readByCpf(String cpf) throws Exception {
        arquivo.seek(4);
        while (arquivo.getFilePointer() < arquivo.length()) {
            char lapide = arquivo.readChar();
            int tam = arquivo.readInt();
            byte[] ba = new byte[tam];
            arquivo.read(ba);

            if (lapide == ' ') {
                T obj = construtor.newInstance();
                obj.fromByteArray(ba);
                
                try {
                    java.lang.reflect.Method getCpfMethod = obj.getClass().getMethod("getCpf");
                    String objCpf = (String) getCpfMethod.invoke(obj);
                    if (objCpf.equals(cpf)) {
                        return obj;
                    }
                } catch (Exception e) {

                }
            }
        }
        return null;
    }

    public ArrayList<T> readAll() throws Exception {
        ArrayList<T> lista = new ArrayList<>();
        arquivo.seek(4);
        while (arquivo.getFilePointer() < arquivo.length()) {
            char lapide = arquivo.readChar();
            int tam = arquivo.readInt();
            byte[] ba = new byte[tam];
            arquivo.read(ba);

            if (lapide == ' ') {
                T obj = construtor.newInstance();
                obj.fromByteArray(ba);
                lista.add(obj);
            }
        }
        return lista;
    }

    public boolean delete(int id) throws Exception {
        arquivo.seek(4);
        while (arquivo.getFilePointer() < arquivo.length()) {
            long pos = arquivo.getFilePointer();
            char lapide = arquivo.readChar();
            int tam = arquivo.readInt();
            byte[] ba = new byte[tam];
            arquivo.read(ba);

            if (lapide == ' ') {
                T obj = construtor.newInstance();
                obj.fromByteArray(ba);
                if (obj.getId() == id) {
                    arquivo.seek(pos);
                    arquivo.writeChar('*');
                    return true;
                }
            }
        }
        return false;
    }

    //deletar cpf
    public boolean deleteByCpf(String cpf) throws Exception {
        arquivo.seek(4);
        while (arquivo.getFilePointer() < arquivo.length()) {
            long pos = arquivo.getFilePointer();
            char lapide = arquivo.readChar();
            int tam = arquivo.readInt();
            byte[] ba = new byte[tam];
            arquivo.read(ba);

            if (lapide == ' ') {
                T obj = construtor.newInstance();
                obj.fromByteArray(ba);
                
                try {
                    java.lang.reflect.Method getCpfMethod = obj.getClass().getMethod("getCpf");
                    String objCpf = (String) getCpfMethod.invoke(obj);
                    if (objCpf.equals(cpf)) {
                        arquivo.seek(pos);
                        arquivo.writeChar('*');
                        return true;
                    }
                } catch (Exception e) {
                
                }
            }
        }
        return false;
    }

    public boolean update(T objAtualizado) throws Exception {
        arquivo.seek(4);
        while (arquivo.getFilePointer() < arquivo.length()) {
            long pos = arquivo.getFilePointer();
            char lapide = arquivo.readChar();
            int tam = arquivo.readInt();
            byte[] ba = new byte[tam];
            arquivo.read(ba);

            if (lapide == ' ') {
                T obj = construtor.newInstance();
                obj.fromByteArray(ba);
                if (obj.getId() == objAtualizado.getId()) {
                    byte[] novo = objAtualizado.toByteArray();
                    if (novo.length <= tam) {
                        arquivo.seek(pos + 2 + 4);
                        arquivo.write(novo);
                    } else {
                        arquivo.seek(pos);
                        arquivo.writeChar('*');
                        arquivo.seek(arquivo.length());
                        arquivo.writeChar(' ');
                        arquivo.writeInt(novo.length);
                        arquivo.write(novo);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public void close() throws Exception {
        arquivo.close();
    }

    public long createWithOffset(T obj) throws Exception {
    arquivo.seek(0);
    int ultimoID = arquivo.readInt();
    int novoID = ultimoID + 1;
    arquivo.seek(0);
    arquivo.writeInt(novoID);
    obj.setId(novoID);

    arquivo.seek(arquivo.length());
    long offset = arquivo.getFilePointer();
    
    byte[] ba = obj.toByteArray();
    arquivo.writeChar(' '); 
    arquivo.writeInt(ba.length);
    arquivo.write(ba);
    
    return offset;
}

public T readByOffset(long offset) throws Exception {
    arquivo.seek(offset); //vai direto para o offset
    
    char lapide = arquivo.readChar();
    int tam = arquivo.readInt();
    byte[] ba = new byte[tam];
    arquivo.read(ba);

    if (lapide == ' ') {
        T obj = construtor.newInstance();
        obj.fromByteArray(ba);
        return obj;
    }
    return null; //registro excluído
}
}