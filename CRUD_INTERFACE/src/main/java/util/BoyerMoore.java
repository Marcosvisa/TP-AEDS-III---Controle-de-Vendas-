package util;

import java.util.Arrays;

public class BoyerMoore implements PatternMatcher {
    
    private static final int ALPHABET_SIZE = 256;
    
    @Override
    public String getName() {
        return "Boyer-Moore (Bad Character Heuristic)";
    }
    
    @Override
    public int search(String text, String pattern) {
        if (text == null || pattern == null || pattern.length() > text.length()) {
            return -1;
        }
        
        int n = text.length();
        int m = pattern.length();
        
        if (m == 0) return 0;
        
        // Pré-processamento: tabela do bad character
        int[] badChar = preprocessBadCharacter(pattern);
        
        int s = 0; // s é o deslocamento do padrão em relação ao texto
        
        while (s <= (n - m)) {
            int j = m - 1;
            
            // Compara da direita para a esquerda
            while (j >= 0 && pattern.charAt(j) == text.charAt(s + j)) {
                j--;
            }
            
            if (j < 0) {
                // Padrão encontrado na posição s
                return s;
            } else {
                // Desloca o padrão usando o bad character heuristic
                int shift = j - badChar[text.charAt(s + j)];
                s += Math.max(1, shift);
            }
        }
        
        return -1; // Não encontrado
    }
    
    private int[] preprocessBadCharacter(String pattern) {
        int m = pattern.length();
        int[] badChar = new int[ALPHABET_SIZE];
        
        // Inicializa todas as posições com -1
        Arrays.fill(badChar, -1);
        
        // Preenche com a última ocorrência de cada caractere no padrão
        for (int i = 0; i < m; i++) {
            badChar[pattern.charAt(i)] = i;
        }
        
        return badChar;
    }
    
    // Método auxiliar para contar todas as ocorrências
    public int countOccurrences(String text, String pattern) {
        if (text == null || pattern == null || pattern.length() > text.length() || pattern.length() == 0) {
            return 0;
        }
        
        int count = 0;
        int n = text.length();
        int m = pattern.length();
        int[] badChar = preprocessBadCharacter(pattern);
        
        int s = 0;
        
        while (s <= (n - m)) {
            int j = m - 1;
            
            while (j >= 0 && pattern.charAt(j) == text.charAt(s + j)) {
                j--;
            }
            
            if (j < 0) {
                count++;
                s += (s + m < n) ? m - badChar[text.charAt(s + m)] : 1;
            } else {
                int shift = j - badChar[text.charAt(s + j)];
                s += Math.max(1, shift);
            }
        }
        
        return count;
    }
    
    // Método para mostrar passo a passo (didático)
    public void showSearchSteps(String text, String pattern) {
        System.out.println("\n🔍 PASSO A PASSO - Boyer-Moore:");
        System.out.println("Texto: " + text);
        System.out.println("Padrão: " + pattern);
        
        int n = text.length();
        int m = pattern.length();
        int[] badChar = preprocessBadCharacter(pattern);
        
        System.out.println("\nTabela Bad Character:");
        for (int i = 0; i < 256; i++) {
            if (badChar[i] != -1) {
                System.out.println("  '" + (char)i + "' → última posição: " + badChar[i]);
            }
        }
        
        int s = 0;
        int step = 1;
        
        while (s <= (n - m)) {
            System.out.println("\n--- Passo " + step + " ---");
            System.out.println("Posição atual: " + s);
            
            // Mostra texto e padrão alinhados
            System.out.println("Texto:    " + text);
            System.out.print("Padrão:   ");
            for (int i = 0; i < s; i++) System.out.print(" ");
            System.out.println(pattern);
            
            int j = m - 1;
            while (j >= 0 && pattern.charAt(j) == text.charAt(s + j)) {
                j--;
            }
            
            if (j < 0) {
                System.out.println("✓ PADRÃO ENCONTRADO na posição " + s);
                break;
            } else {
                char mismatchChar = text.charAt(s + j);
                int shift = j - badChar[mismatchChar];
                System.out.println("✗ Mismatch na posição " + j + ": '" + pattern.charAt(j) + "' ≠ '" + mismatchChar + "'");
                System.out.println("  Deslocamento: " + Math.max(1, shift));
                s += Math.max(1, shift);
            }
            
            step++;
            if (step > 20) {
                System.out.println("⚠️  Muitos passos, interrompendo...");
                break;
            }
        }
        
        if (s > (n - m)) {
            System.out.println("✗ PADRÃO NÃO ENCONTRADO");
        }
    }
}