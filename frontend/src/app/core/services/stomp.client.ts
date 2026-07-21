// Minimal STOMP-over-WebSocket client — just the frames this app needs:
// CONNECT (with the JWT), SUBSCRIBE to one destination, and receive MESSAGE frames.
export class StompClient {
  private socket?: WebSocket;
  private closed = false;

  connect(url: string, token: string, destination: string, onMessage: (body: string) => void): void {
    this.closed = false;
    this.open(url, token, destination, onMessage);
  }

  disconnect(): void {
    this.closed = true;
    this.socket?.close();
    this.socket = undefined;
  }

  private open(url: string, token: string, destination: string, onMessage: (body: string) => void): void {
    this.socket = new WebSocket(url, ['v12.stomp', 'v11.stomp']);

    this.socket.onopen = () => this.send('CONNECT', {
      'accept-version': '1.2',
      host: '/',
      'heart-beat': '0,0',
      Authorization: `Bearer ${token}`
    });

    this.socket.onmessage = event => {
      for (const raw of String(event.data).split('\0')) {
        const frame = raw.replace(/^\n+/, '');
        if (!frame) continue;
        const split = frame.indexOf('\n\n');
        const head = split === -1 ? frame : frame.substring(0, split);
        const body = split === -1 ? '' : frame.substring(split + 2);
        const command = head.split('\n', 1)[0];

        if (command === 'CONNECTED') {
          this.send('SUBSCRIBE', { id: 'sub-0', destination });
        } else if (command === 'MESSAGE') {
          onMessage(body);
        }
      }
    };

    // Reconnect on an unexpected drop, unless we closed it ourselves (logout).
    this.socket.onclose = () => {
      if (!this.closed) {
        setTimeout(() => this.open(url, token, destination, onMessage), 5000);
      }
    };
  }

  private send(command: string, headers: Record<string, string>, body = ''): void {
    const lines = [command, ...Object.entries(headers).map(([k, v]) => `${k}:${v}`)];
    this.socket?.send(lines.join('\n') + '\n\n' + body + '\0');
  }
}
