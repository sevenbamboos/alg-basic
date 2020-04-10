package com.samwang.anki.impl.model.token;

import com.samwang.anki.impl.model.CardType;

public final class TokenContext {
    public final String delim;
    public final CardType type;
    public final boolean inGroup;

    public TokenContext(CardType type) {
        this(" ", type, false);
    }

    public TokenContext(String delim, CardType type, boolean inGroup) {
        this.delim = delim;
        this.type = type;
        this.inGroup = inGroup;
    }

    public TokenContext setDelim(String value) {
        return new TokenContext(value, type, inGroup);
    }

    public TokenContext setType(CardType value) {
        return new TokenContext(delim, value, inGroup);
    }

    public TokenContext setInGroup(boolean value) {
        return new TokenContext(delim, type, value);
    }
}
