/*
 * Copyright (c) 2006-2025 Chris Collins
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.hitorro.conceptnet5;

/**
 * Types of things:
 * <p>
 * 1) Get me all the concepts for a word, language, v|n a) constrain by a subset of relationship types b) dont constrain
 * by language
 * <p>
 * What languages did you find What concepts did you find what types of relations did you find
 * <p>
 * c) constrain target concepts by some criteria (string match ops, language, v/n)
 * <p>
 * d) order results by quality metric
 * <p>
 * 2) query for words with some string match op other than exact.  Such as: a) LED b) prefix c) filter/normalization d)
 * Phonetic
 * <p>
 * 3 ) provide macro queries where its concept of a concept (expansion)
 * <p>
 * <p>
 * Macro features: 1) find me a synonym in target language x (perhaps a translation)
 */
public class Query {
}
