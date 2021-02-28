# File: statements.py
# Template file for Informatics 2A Assignment 2:
# 'A Natural Language Query System in Python/NLTK'

# John Longley, November 2012
# Revised November 2013 and November 2014 with help from Nikolay Bogoychev
# Revised November 2015 by Toms Bergmanis
# Revised October 2017 by Chunchuan Lyu


# PART A: Processing statements

def add(lst,item):
    if (item not in lst):
        lst.insert(len(lst),item)

class Lexicon:
    """stores known word stems of various part-of-speech categories"""
    # add code here
    #LexStem =[]
    #LexCat = []
    Lex =[]
    LexStem = []
    currentWord = 0
    def add(self, stem, cat):
        #self.LexStem[currentWord] = stem
        #LexCat.insert(cat)
        self.Lex.insert((stem, cat))
        return

    def getAll(self, cat):
        for x,y in enumerate(self.Lex):
            if x not in self.LexStem:
                self.LexStem.insert(x)
        print(self.LexStem)
        return




class FactBase:
    """stores unary and binary relational facts"""
    # add code here
    Unary = []
    Binary = []

    def addUnary(self, pred, e1):
        self.Unary.insert((pred, e1))

    def addBinary(self, pred, e1, e2):
        self.Binary.insert((pred, e1, e2))

    def queryUnary(self, pred, e1):
        if (pred, e1) in self.Unary:
            return True
        else:
            return False

    def queryBinary(self, pred, e1, e2):
        if (pred, e1, e2) in self.Binary:
            return True
        else:
            return False


import re
from nltk.corpus import brown 
def verb_stem(s):
    """extracts the stem from the 3sg form of a verb, or returns empty string"""
    # add code here
    stringOut = ""

    
    if re.match("[a-z]*([^sxyzaeiou]|[^cs]h)s$", s):
        stringOut = s[:-1]
    #elif re.match("([a-z]*(c|s)([a-r]|[t-w]))|([a-z]*")
    elif re.match("[a-z]*[aeiou]ys$", s):
        stringOut = s[:-1]
    elif re.match("[a-z]+[^aeiou]ies$", s):
        stringOut = s[:-3] + "y"
    elif re.match("[^aeiou]ies$", s):
        stringOut = s[:-1]
    elif re.match("[a-z]*(o|x|ch|sh|ss|zz)es$", s):
        stringOut = s[:-2]
    elif re.match("[a-z]*(([^s]se)|([^z]ze))s$", s):
        stringOut = s[:-1]
    elif re.match("has", s):
        stringOut = "have"
    elif re.match("[a-z]*([^iosxz]|[^cs]h)es$", s):
        stringOut = s[:-1]
    else:
        stringOut = ""

    for x,y in nltk.corpus.brown.tagged_words():
        if (x == s and y == "VBZ" and stringOut != "" | x == stringOut and y == "VB" and stringOut != ""):
            return stringOut
        else:
            return ""

def add_proper_name (w,lx):
    """adds a name to a lexicon, checking if first letter is uppercase"""
    if ('A' <= w[0] and w[0] <= 'Z'):
        lx.add(w,'P')
        return ''
    else:
        return (w + " isn't a proper name")

def process_statement (lx,wlist,fb):
    """analyses a statement and updates lexicon and fact base accordingly;
       returns '' if successful, or error message if not."""
    # Grammar for the statement language is:
    #   S  -> P is AR Ns | P is A | P Is | P Ts P
    #   AR -> a | an
    # We parse this in an ad hoc way.
    msg = add_proper_name (wlist[0],lx)
    if (msg == ''):
        if (wlist[1] == 'is'):
            if (wlist[2] in ['a','an']):
                lx.add (wlist[3],'N')
                fb.addUnary ('N_'+wlist[3],wlist[0])
            else:
                lx.add (wlist[2],'A')
                fb.addUnary ('A_'+wlist[2],wlist[0])
        else:
            stem = verb_stem(wlist[1])
            if (len(wlist) == 2):
                lx.add (stem,'I')
                fb.addUnary ('I_'+stem,wlist[0])
            else:
                msg = add_proper_name (wlist[2],lx)
                if (msg == ''):
                    lx.add (stem,'T')
                    fb.addBinary ('T_'+stem,wlist[0],wlist[2])
    return msg
                        
# End of PART A.

