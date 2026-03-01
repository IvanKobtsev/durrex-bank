import { useMatch } from 'react-router-dom';
import { appendTrailingChars, removeTrailingChars } from './string-helpers';

/**
 * This hook is a wrapper of ReactRouter's `useMatch` hook that automatically
 * appends or removes the trailing `*` to allow for exact path matching.
 * @param path
 * @param exact
 */
export function useRouteMatch(path: string, exact = false) {
  return useMatch(
    exact ? removeTrailingChars(path, '*') : appendTrailingChars(path, '*'),
  );
}
