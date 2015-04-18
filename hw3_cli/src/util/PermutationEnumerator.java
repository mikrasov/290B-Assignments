/*
 * The MIT License
 *
 * Copyright 2015 cappello.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Pete Cappello
 * @param <T> the type of objectList being permuted.
 */
public class PermutationEnumerator<T> implements Iterable< List<T> >
{
    private PermutationEnumerator<T> subPermutationEnumerator;
    private List<T> permutation;
    private List<T> subpermutation;
    private int nextIndex = 0;
    private T interleaveObject;
    
    final static Integer ONE = 1;
    final static Integer TWO = 2;
    
    /**
     *
     * @param objectList the objectList being permuted is unmodified.
     * @throws java.lang.IllegalArgumentException when passed a null object list.
     */
    public PermutationEnumerator( final List<T> objectList ) throws IllegalArgumentException
    {
        if ( objectList == null )
        {
            throw new IllegalArgumentException();
        }
        permutation = new ArrayList<>( objectList );
        if ( permutation.isEmpty() )
        { 
            return; 
        }
        subpermutation = new ArrayList<>( permutation );
        interleaveObject = subpermutation.remove( 0 );
        subPermutationEnumerator = new PermutationEnumerator<>( subpermutation );
        subpermutation = subPermutationEnumerator.nextPerm();
    }
    
    /**
     * Produce the permutation permutation.
     * @return the permutation permutation as a List.
     * If none, returns null.
     * @throws java.lang.IllegalArgumentException  permutation() invoked when hasNext() is false.
     */
    private List<T> nextPerm() throws IllegalArgumentException
    {
        if ( permutation == null )
        {
            return null;
        }
        List<T> returnValue = new ArrayList<>( permutation );
        if ( permutation.isEmpty() )
        {
            permutation = null;
        }
        else if ( nextIndex < permutation.size() - 1)
        {
            T temp = permutation.get( nextIndex + 1 );
            permutation.set( nextIndex + 1, permutation.get( nextIndex ) );
            permutation.set( nextIndex++, temp );
        }
        else
        {   
            subpermutation = subPermutationEnumerator.nextPerm();
            if ( subpermutation == null || subpermutation.isEmpty() )
            {
                permutation = null;
            }
            else
            {
                permutation = new ArrayList<>( subpermutation );
                permutation.add( 0, interleaveObject );                
                nextIndex = 0;
            }
        }
        return returnValue;
    }

	@Override
	public Iterator<List<T>> iterator() {
		return new Iterator<List<T>>() {

			@Override
			public boolean hasNext() { return permutation != null ;}

			@Override
			public List<T> next() { return nextPerm(); }
			
			@Override
			public void remove(){ throw new UnsupportedOperationException();}
		};
	}
}