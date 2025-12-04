package util;

public class KMP implements PatternMatcher {
    
    @Override
    public String getName() {
        return "Knuth-Morris-Pratt (KMP)";
    }
    
    @Override
    public int search(String text, String pattern) {
        if (text == null || pattern == null || pattern.length() > text.length()) {
            return -1;
        }
        
        int n = text.length();
        int m = pattern.length();
        
        //se o padrão for vazio, retorna 0
        if (m == 0) return 0;
        
        //preprocessamento: cria o array de prefixos (lps)
        int[] lps = computeLPSArray(pattern);
        
        int i = 0; // índice para text
        int j = 0; // índice para pattern
        
        while (i < n) {
            if (pattern.charAt(j) == text.charAt(i)) {
                i++;
                j++;
            }
            
            if (j == m) {
                // Padrão encontrado na posição i-j
                return i - j;
            } else if (i < n && pattern.charAt(j) != text.charAt(i)) {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }
        
        return -1; // Não encontrado
    }
    
    private int[] computeLPSArray(String pattern) {
        int m = pattern.length();
        int[] lps = new int[m];
        
        int length = 0; // tamanho do maior prefixo sufixo anterior
        int i = 1;
        
        while (i < m) {
            if (pattern.charAt(i) == pattern.charAt(length)) {
                length++;
                lps[i] = length;
                i++;
            } else {
                if (length != 0) {
                    length = lps[length - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }
        
        return lps;
    }
    
    //método auxiliar para contar todas as ocorrências
    public int countOccurrences(String text, String pattern) {
        if (text == null || pattern == null || pattern.length() > text.length() || pattern.length() == 0) {
            return 0;
        }
        
        int count = 0;
        int n = text.length();
        int m = pattern.length();
        int[] lps = computeLPSArray(pattern);
        
        int i = 0;
        int j = 0;
        
        while (i < n) {
            if (pattern.charAt(j) == text.charAt(i)) {
                i++;
                j++;
            }
            
            if (j == m) {
                count++;
                j = lps[j - 1];
            } else if (i < n && pattern.charAt(j) != text.charAt(i)) {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }
        
        return count;
    }
}