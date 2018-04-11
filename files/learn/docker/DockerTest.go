package main

import (
	"fmt"
	"os"
)

func main() {
	fmt.Println(os.Args[1])

	for i := 0; i < 10; i++ {
		fmt.Printf("Hello Docker%d!\n", i)
	}
}
