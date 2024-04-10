# quick-mac

An interactive app-centric network and system analytics CLI tool for macOS.

![me](https://github.com/aniketraj1947/quick-mac/blob/main/src/main/resources/tty.gif)

## Installation
 
```bash
brew tap aniketraj1947/tap
brew install quick-mac
```

## Commands

### Network
| Command         | Description                                                                               |
| :------------ | :-------------- |
| wifi            | Get connected WiFi data, including signal strength and noise.                              |
| isp-info        | Get ISP related data for the public IP address in the connected network.                    |
| network-usage   | Get app-specific aggregated network usage (egress/ingress) in the last few seconds.        |
| speed-test      | Get download/upload speed, latency, and jitter.                                            |
| dns             | Get nameservers, search domains, and other DNS related information.                         |

### OS & System

| Command     | Description                                                                   |
| :------------ | :-------------- |
| memory      | Display top applications by their RAM usage.                                  |
| cpu         | Get an aggregated application-centered view of applications by their CPU usage.|
| battery     | Get live battery-related data, including cycle count and remaining time.      |
| sys-info    | Get system hardware-related information, including CPU, Memory, and Disk Usage.|


