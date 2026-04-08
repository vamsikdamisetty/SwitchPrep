package com.trie;

/**
 * WordDictionary - A Trie-based data structure that supports:
 *   1. Adding words
 *   2. Searching words with exact match or wildcard '.' (matches any single character)
 *
 * LeetCode 211 - Design Add and Search Words Data Structure
 *
 * Time Complexity:
 *   addWord  -> O(L) where L = length of the word
 *   search   -> O(L) for exact match, O(26^L) worst-case for all wildcards
 * Space Complexity: O(N * L) where N = number of words inserted
 */
class WordDictionary {

    // Root node of the Trie – does not hold a character itself
    private Node root;

    /** Initialize the data structure with an empty Trie. */
    public WordDictionary() {
        root = new Node();
    }

    /**
     * Inserts a word into the Trie character by character.
     * Creates new nodes as needed and marks the last node as end-of-word.
     */
    public void addWord(String word) {
        Node node = root;
        for (char ch : word.toCharArray()) {
            // If the character link doesn't exist, create a new node
            if (!node.containsKey(ch)) {
                node.put(ch, new Node());
            }
            // Move to the next node
            node = node.get(ch);
        }
        // Mark the end of the word
        node.setEnd();
    }

    /** Public search entry point – delegates to the recursive helper starting from root. */
    public boolean search(String word) {
        return search(word, root);
    }

    /**
     * Recursively searches for a word in the Trie.
     * '.' acts as a wildcard that matches any single character –
     * when encountered, we try all 26 possible children.
     */
    public boolean search(String word, Node node) {

        for (int index = 0; index < word.length(); index++) {
            char ch = word.charAt(index);

            // Wildcard handling: try every non-null child
            if (ch == '.') {
                Node[] links = node.getLinks();
                for (int i = 0; i < 26; i++) {
                    if (links[i] != null && search(word.substring(index + 1), links[i])) {
                        return true;
                    }
                }
                return false; // None of the children led to a match
            }

            // Exact character: if not present, word doesn't exist
            if (!node.containsKey(ch)) {
                return false;
            }

            // Advance to the child node for this character
            node = node.get(ch);
        }
        // We've consumed all characters – check if this node marks end-of-word
        return node.isEnd();
    }

    public static void main(String[] args) {

        WordDictionary dict = new WordDictionary();

        // --- Add words ---
        dict.addWord("bad");
        dict.addWord("bat");
        dict.addWord("dad");
        dict.addWord("mad");
        dict.addWord("pad");
        dict.addWord("badge");
        System.out.println("Words added: bad, bat, dad, mad, pad, badge");

        // --- Exact searches ---
        System.out.println("\n--- Exact Searches ---");
        System.out.println("search(\"bad\")   = " + dict.search("bad"));   // true
        System.out.println("search(\"bat\")   = " + dict.search("bat"));   // true
        System.out.println("search(\"bed\")   = " + dict.search("bed"));   // false
        System.out.println("search(\"badge\") = " + dict.search("badge")); // true
        System.out.println("search(\"ba\")    = " + dict.search("ba"));    // false (prefix only, not a full word)

        // --- Wildcard searches (. matches any single character) ---
        System.out.println("\n--- Wildcard Searches ---");
        System.out.println("search(\".ad\")   = " + dict.search(".ad"));   // true  -> matches bad, dad, mad, pad
        System.out.println("search(\"b..\")   = " + dict.search("b.."));   // true  -> matches bad, bat
        System.out.println("search(\"b.d\")   = " + dict.search("b.d"));   // true  -> matches bad
        System.out.println("search(\"..t\")   = " + dict.search("..t"));   // true  -> matches bat
        System.out.println("search(\"...\")   = " + dict.search("..."));   // true  -> matches any 3-letter word
        System.out.println("search(\"....\")  = " + dict.search("...."));  // false -> no 4-letter words except badge (5)
        System.out.println("search(\".....\") = " + dict.search(".....")); // true  -> matches badge

        // --- Edge cases ---
        System.out.println("\n--- Edge Cases ---");
        System.out.println("search(\".\")     = " + dict.search("."));     // false -> no single-char words
        System.out.println("search(\"z..\")   = " + dict.search("z.."));   // false -> no word starting with z
    }
}

