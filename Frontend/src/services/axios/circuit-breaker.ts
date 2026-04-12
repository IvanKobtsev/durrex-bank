const CIRCUIT_BREAKER_OPEN_UNTIL_KEY = "circuit-breaker-open-until";
const REQUEST_WINDOW_MS = 60_000;
const BREAKER_OPEN_MS = 60_000;
const FAILURE_THRESHOLD = 0.7;

type RequestOutcome = {
  timestampMs: number;
  isRefusedOr503Failure: boolean;
};

type CircuitBreakerListener = () => void;

const listeners = new Set<CircuitBreakerListener>();
const requestOutcomes: RequestOutcome[] = [];

let openUntilMs: number | null = readPersistedOpenUntil();

export class CircuitBreakerOpenError extends Error {
  constructor(message = "Circuit breaker is open") {
    super(message);
    this.name = "CircuitBreakerOpenError";
  }
}

function readPersistedOpenUntil(): number | null {
  const persisted = localStorage.getItem(CIRCUIT_BREAKER_OPEN_UNTIL_KEY);
  if (!persisted) {
    return null;
  }

  const parsed = Number.parseInt(persisted, 10);
  if (!Number.isFinite(parsed)) {
    localStorage.removeItem(CIRCUIT_BREAKER_OPEN_UNTIL_KEY);
    return null;
  }

  if (parsed <= Date.now()) {
    localStorage.removeItem(CIRCUIT_BREAKER_OPEN_UNTIL_KEY);
    return null;
  }

  return parsed;
}

function pruneOldOutcomes(nowMs: number): void {
  while (requestOutcomes.length > 0) {
    if (requestOutcomes[0].timestampMs > nowMs - REQUEST_WINDOW_MS) {
      return;
    }

    requestOutcomes.shift();
  }
}

function notifyListeners(): void {
  listeners.forEach((listener) => listener());
}

function persistOpenUntil(value: number | null): void {
  if (value === null) {
    localStorage.removeItem(CIRCUIT_BREAKER_OPEN_UNTIL_KEY);
    return;
  }

  localStorage.setItem(CIRCUIT_BREAKER_OPEN_UNTIL_KEY, String(value));
}

function openCircuitBreaker(nowMs: number): void {
  const nextOpenUntil = nowMs + BREAKER_OPEN_MS;
  if (openUntilMs !== null && openUntilMs >= nextOpenUntil) {
    return;
  }

  openUntilMs = nextOpenUntil;
  persistOpenUntil(openUntilMs);
  notifyListeners();
}

function closeCircuitBreaker(): void {
  if (openUntilMs === null) {
    return;
  }

  openUntilMs = null;
  persistOpenUntil(null);
  notifyListeners();
}

function isFailureRateAboveThreshold(): boolean {
  if (requestOutcomes.length === 0) {
    return false;
  }

  const failuresCount = requestOutcomes.reduce(
    (sum, outcome) => sum + Number(outcome.isRefusedOr503Failure),
    0,
  );
  return failuresCount / requestOutcomes.length >= FAILURE_THRESHOLD;
}

export function isCircuitBreakerOpen(nowMs = Date.now()): boolean {
  if (openUntilMs === null) {
    return false;
  }

  if (openUntilMs <= nowMs) {
    closeCircuitBreaker();
    return false;
  }

  return true;
}

export function getCircuitBreakerOpenUntilMs(nowMs = Date.now()): number | null {
  if (!isCircuitBreakerOpen(nowMs)) {
    return null;
  }

  return openUntilMs;
}

export function getCircuitBreakerRemainingMs(nowMs = Date.now()): number {
  const openUntil = getCircuitBreakerOpenUntilMs(nowMs);
  if (openUntil === null) {
    return 0;
  }

  return Math.max(0, openUntil - nowMs);
}

export function recordRequestOutcome(
  isRefusedOr503Failure: boolean,
  nowMs = Date.now(),
): void {
  pruneOldOutcomes(nowMs);
  requestOutcomes.push({ timestampMs: nowMs, isRefusedOr503Failure });

  if (!isCircuitBreakerOpen(nowMs) && isFailureRateAboveThreshold()) {
    openCircuitBreaker(nowMs);
  }
}

export function subscribeToCircuitBreaker(listener: CircuitBreakerListener): () => void {
  listeners.add(listener);
  return () => listeners.delete(listener);
}

