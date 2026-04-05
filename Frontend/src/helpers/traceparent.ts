export function generateTraceparent(): string {
  function randomHex(bytes: number): string {
    const array = new Uint8Array(bytes);
    crypto.getRandomValues(array);
    return Array.from(array, (b) => b.toString(16).padStart(2, "0")).join("");
  }

  const traceId = randomHex(16); // 32 hex chars
  const spanId = randomHex(8); // 16 hex chars

  const version = "00";
  const traceFlags = "01"; // sampled

  return `${version}-${traceId}-${spanId}-${traceFlags}`;
}
